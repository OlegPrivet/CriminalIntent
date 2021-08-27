package com.example.criminalintent.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.R
import com.example.criminalintent.adapters.baseadapter.BaseAdapterCallback
import com.example.criminalintent.adapters.crimeadapter.CrimeRecycleAdapter
import com.example.criminalintent.adapters.crimeadapter.CrimeListAdapter
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
    private val adapterRA: CrimeRecycleAdapter = CrimeRecycleAdapter()
    private val adapterLA: CrimeListAdapter = CrimeListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.title = "Crime List"
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = this.rootView?.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapterRA
        adapterRA.attachCallback(callback = object : BaseAdapterCallback<Crime>{
            override fun onItemClick(model: Crime, view: View) {
                val action = CrimeListFragmentDirections.actionCrimeListFragmentToCrimeFragment(crimeId = model.id)
                Navigation.findNavController(rootView!!).navigate(action)
            }

            override fun onLongClick(model: Crime, view: View): Boolean {
                return false
            }

        })
        adapterLA.attachCallback(callback = object :BaseAdapterCallback<Crime>{
            override fun onItemClick(model: Crime, view: View) {
                val action = CrimeListFragmentDirections.actionCrimeListFragmentToCrimeFragment(crimeId = model.id)
                Navigation.findNavController(rootView!!).navigate(action)
            }

            override fun onLongClick(model: Crime, view: View): Boolean {
                return false
            }

        })

        /*val b = true
        val adapter = when (b) {
            true -> adapter         //фича для определения адаптера!!!!!!
            else -> adapterLA
        }*/


        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer { crimes ->
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