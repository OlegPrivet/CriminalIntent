package com.example.criminalintent.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.criminalintent.R
import com.example.criminalintent.models.Crime
import com.example.criminalintent.utils.Utils
import com.example.criminalintent.viewmodels.CrimeDetailViewModel
import com.google.android.material.appbar.MaterialToolbar
import java.util.*

class CrimeFragment : Fragment() {

    private lateinit var crime: Crime
    private lateinit var crimeId: UUID
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCH: CheckBox
    private lateinit var toolbar: MaterialToolbar

    private var rootView: View? = null
    private val args by navArgs<CrimeFragmentArgs>()

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

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
        toolbar = rootView!!.findViewById(R.id.toolbar)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        titleField = rootView?.findViewById(R.id.crime_title) as EditText
        dateButton = rootView?.findViewById(R.id.crime_date) as Button
        solvedCH = rootView?.findViewById(R.id.crime_solved) as CheckBox
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            setDate(crime.date)
        }

    }

    private fun setDate(date: Date) {
        val c = Calendar.getInstance()
        c.time = date
        val datePick = DatePickerDialog(
            requireContext(), {pickerDate, year, month, day ->
                val timePicker = TimePickerDialog(requireContext(),
                    { pickerTime, hour, minute ->
                        crime.date = GregorianCalendar(year, month, day, hour, minute).time
                        crimeDetailViewModel.saveCrime(crime)

                    },
                    c.get(Calendar.HOUR),
                    c.get(Calendar.MINUTE),
                    true
                ).show()
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        ).show()
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