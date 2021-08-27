package com.example.criminalintent.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.criminalintent.database.CrimeDB
import com.example.criminalintent.models.Crime
import java.util.*

private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context: Context){

    private val database: CrimeDB = Room.databaseBuilder(
        context.applicationContext,
        CrimeDB::class.java,
        DATABASE_NAME
    ).build()

    private val crimeDao = database.crimeDao()

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(uuid: UUID): LiveData<Crime?> = crimeDao.getCrime(id = uuid)

    suspend fun updateCrime(crime: Crime){
        crimeDao.updateCrime(crime)
    }

    suspend fun addCrime(crime: Crime){
        crimeDao.addCrime(crime)
    }

    suspend fun deleteCrime(crime: Crime){
        crimeDao.deleteCrime(crime)
    }

    companion object{
        private var INSTANCE: CrimeRepository? = null
        fun initialize(context: Context){
            if (INSTANCE == null){
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository{
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }

    }

}