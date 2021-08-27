package com.example.criminalintent.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criminalintent.models.Crime
import com.example.criminalintent.repositories.CrimeRepository
import kotlinx.coroutines.launch

class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    val  crimeListLiveData = crimeRepository.getCrimes()

    fun deleteCrime(crime: Crime) = viewModelScope.launch {
        crimeRepository.deleteCrime(crime)
    }

}