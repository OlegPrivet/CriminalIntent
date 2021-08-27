package com.example.criminalintent.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.criminalintent.R
import com.example.criminalintent.models.Crime
import com.example.criminalintent.utils.Utils
import com.example.criminalintent.viewmodels.CrimeDetailViewModel
import com.example.criminalintent.viewmodels.SharedDatePickerViewModel
import java.util.*

class CrimeFragment : Fragment() {

    private lateinit var crime: Crime
    private lateinit var crimeId: UUID
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCH: CheckBox

    private var rootView: View? = null
    private val args by navArgs<CrimeFragmentArgs>()

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }
    private val sharedDatePickerViewModel: SharedDatePickerViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        crimeId = args.crimeId
        crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = rootView?.findViewById(R.id.crime_title) as EditText
        dateButton = rootView?.findViewById(R.id.crime_date) as Button
        solvedCH = rootView?.findViewById(R.id.crime_solved) as CheckBox
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedDatePickerViewModel.date.observe(viewLifecycleOwner, { date ->
            date?.let {
                this.crime.date = date
                updateUI()
            }
        })
        crimeDetailViewModel.crimeLiveData.observe(viewLifecycleOwner, { crime ->
            crime?.let {
                this.crime = crime
                updateUI()
            }
        })

    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(ch: CharSequence?, p1: Int, p2: Int, p3: Int) {
                crime.title = ch.toString()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        titleField.addTextChangedListener(titleWatcher)
        solvedCH.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
                crimeDetailViewModel.saveCrime(crime)
            }
        }
        dateButton.setOnClickListener {
            val action = CrimeFragmentDirections.actionCrimeFragmentToDatePickerFragment(date = crime.date)
            Navigation.findNavController(rootView!!).navigate(action)
        }

    }

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.apply {
            text = Utils.dateFormat(crime.date)
        }
        solvedCH.isChecked = crime.isSolved
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

}