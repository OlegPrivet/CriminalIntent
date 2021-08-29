package com.example.criminalintent.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.format.DateFormat.format
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.criminalintent.R
import com.example.criminalintent.models.Crime
import com.example.criminalintent.utils.PictureUtils.Companion.getScaledBitmap
import com.example.criminalintent.utils.Utils
import com.example.criminalintent.viewmodels.CrimeDetailViewModel
import com.jakewharton.rxbinding.widget.RxTextView
import rx.functions.Action1
import java.io.File
import java.util.*

private const val DATE_FORMAT = "EEE, MMM, dd"
private const val AUTHORITIES = "com.example.criminalintent.fileprovider"

class CrimeFragment : Fragment() {

    private lateinit var crime: Crime
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    private lateinit var crimeId: UUID
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var photoButton: Button
    private lateinit var solvedCH: CheckBox
    private lateinit var toolbar: Toolbar
    private lateinit var crimeImage: ImageView

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
        toolbar.inflateMenu(R.menu.crime_top_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete_crime -> {
                    crimeDetailViewModel.deleteCrime(crime)
                    requireActivity().onBackPressed()
                    true
                }
                else -> false
            }
        }
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        titleField = rootView?.findViewById(R.id.crime_title) as EditText
        dateButton = rootView?.findViewById(R.id.crime_date) as Button
        reportButton = rootView?.findViewById(R.id.crime_report) as Button
        suspectButton = rootView?.findViewById(R.id.crime_suspect) as Button
        solvedCH = rootView?.findViewById(R.id.crime_solved) as CheckBox
        crimeImage = rootView?.findViewById(R.id.crime_image) as ImageView
        photoButton = rootView?.findViewById(R.id.take_photo) as Button
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(viewLifecycleOwner, { crime ->
            crime?.let {
                this.crime = crime
                photoFile = crimeDetailViewModel.getPhotoFile(crime)
                photoUri = FileProvider.getUriForFile(requireActivity(), AUTHORITIES, photoFile)
                updateUI()
            }
        })
        val contactResult =
            registerForActivityResult(ActivityResultContracts.PickContact()) {
                it?.let {
                    val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                    val cursor = requireActivity().contentResolver
                        .query(it, queryFields, null, null, null)
                    cursor?.use { cur ->
                        if (cur.count == 0) return@registerForActivityResult
                        cur.moveToFirst()
                        crime.suspect = cur.getString(0)
                        crimeDetailViewModel.saveCrime(crime)
                    }
                }
            }
        suspectButton.apply {
            setOnClickListener {
                contactResult.launch(null)
            }
        }
        val imageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                requireActivity().revokeUriPermission(
                    photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                updatePhotoView()
            }
        }
        photoButton.apply {
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }
            setOnClickListener {
                val cameraActivities: List<ResolveInfo> = packageManager.queryIntentActivities(
                    captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY
                )
                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                imageResult.launch(photoUri)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        RxTextView.editorActions(titleField).subscribe(Action1 {
            if (it == EditorInfo.IME_ACTION_DONE) {
                crime.title = titleField.text.toString()
                crimeDetailViewModel.saveCrime(crime)
                titleField.hideKeyboard()
            }
        })
        solvedCH.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
                crimeDetailViewModel.saveCrime(crime)
            }
        }
        dateButton.setOnClickListener {
            setDate(crime.date)
        }
        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }
    }

    private fun setDate(date: Date) {
        val c = Calendar.getInstance()
        c.time = date
        DatePickerDialog(
            requireContext(), { pickerDate, year, month, day ->
                TimePickerDialog(
                    requireContext(),
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
        suspectButton.apply {
            text = if (crime.suspect.isEmpty()) "Choose Suspect"
            else crime.suspect
        }
        updatePhotoView()
    }

    private fun updatePhotoView() {
        if (photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, requireActivity())
            crimeImage.setImageBitmap(bitmap)
        } else {
            crimeImage.setImageResource(R.drawable.ic_add_photo)
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = format(DATE_FORMAT, crime.date).toString()
        val suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

}