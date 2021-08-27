package com.example.criminalintent.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class SharedDatePickerViewModel:ViewModel() {

    private var _date = MutableLiveData<Date>(Date())
    var date:LiveData<Date> = _date

    fun saveDate(newDate: Date){
        _date.value = newDate
    }
}