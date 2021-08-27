package com.example.criminalintent.viewmodels

import androidx.lifecycle.*
import com.example.criminalintent.models.Crime
import com.example.criminalintent.repositories.CrimeRepository
import kotlinx.coroutines.launch
import java.util.*

class CrimeDetailViewModel() : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIdLiveData) { crimeId ->
            crimeRepository.getCrime(crimeId)
        }

    fun loadCrime(crimeId: UUID){
        crimeIdLiveData.value = crimeId
    }

    fun saveCrime(crime: Crime) = viewModelScope.launch {
        crimeRepository.updateCrime(crime)
    }

    fun addCrime(crime: Crime) = viewModelScope.launch {
        crimeRepository.addCrime(crime)
    }

}