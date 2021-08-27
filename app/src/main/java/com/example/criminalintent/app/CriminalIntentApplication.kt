package com.example.criminalintent.app

import android.app.Application
import com.example.criminalintent.repositories.CrimeRepository

class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(context = this)
    }

}