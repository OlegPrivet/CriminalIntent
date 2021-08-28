package com.example.criminalintent.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.R
import com.example.criminalintent.adapters.baseadapter.BaseAdapterCallback
import com.example.criminalintent.adapters.crimeadapter.CrimeListAdapter
import com.example.criminalintent.adapters.crimeadapter.CrimeRecycleAdapter
import com.example.criminalintent.adapters.diffutils.CrimeListDiffUtils
import com.example.criminalintent.models.Crime
import com.example.criminalintent.viewmodels.CrimeListViewModel

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    private var rootView: View? = null
    private lateinit var crimeRecyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private val adapterRA: CrimeRecycleAdapter = CrimeRecycleAdapter()
    private val adapterLA: CrimeListAdapter = CrimeListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_crime_list, container, false)
        toolbar = rootView!!.findViewById(R.id.toolbar)
        toolbar.inflateMenu(R.menu.crime_list_top_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.new_crime -> {
                    val crime = Crime()
                    crimeListViewModel.addCrime(crime)
                    val action =
                        CrimeListFragmentDirections.actionCrimeListFragmentToCrimeFragment(crimeId = crime.id)
                    Navigation.findNavController(rootView!!).navigate(action)
                    true
                }
                else -> false
            }
        }
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        crimeRecyclerView = this.rootView?.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapterRA
        adapterRA.attachCallback(callback = object : BaseAdapterCallback<Crime> {
            override fun onItemClick(model: Crime, view: View) {
                val action =
                    CrimeListFragmentDirections.actionCrimeListFragmentToCrimeFragment(crimeId = model.id)
                Navigation.findNavController(rootView!!).navigate(action)
            }

            override fun onLongClick(model: Crime, view: View): Boolean {
                return false
            }

        })
        adapterLA.attachCallback(callback = object : BaseAdapterCallback<Crime> {
            override fun onItemClick(model: Crime, view: View) {
                val action =
                    CrimeListFragmentDirections.actionCrimeListFragmentToCrimeFragment(crimeId = model.id)
                Navigation.findNavController(rootView!!).navigate(action)
            }

            override fun onLongClick(model: Crime, view: View): Boolean {
                return false
            }

        })

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner, { crimes ->
                crimes?.let {
                    updateUI(crimes)
                }
            }
        )
    }

    private fun updateUI(crimes: List<Crime>) {
        /*adapterLA.submitList(crimes)
        crimeRecyclerView.adapter = adapterLA*/
        val crimeListDiffUtils = CrimeListDiffUtils(adapterRA.getData(), crimes)
        val diffRes = DiffUtil.calculateDiff(crimeListDiffUtils)
        adapterRA.setList(crimes)
        diffRes.dispatchUpdatesTo(adapterRA)
    }

    override fun onDetach() {
        super.onDetach()
        adapterRA.detachCallback()
        adapterLA.detachCallback()
    }

}