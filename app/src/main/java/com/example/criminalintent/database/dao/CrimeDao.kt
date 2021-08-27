package com.example.criminalintent.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.criminalintent.models.Crime
import java.util.*

@Dao
interface CrimeDao {
    @Query("Select * FROM crime")
    fun getCrimes():LiveData<List<Crime>>
    @Query("Select * FROM crime WHERE id = (:id)")
    fun getCrime(id: UUID):LiveData<Crime?>
    @Update
    suspend fun updateCrime(crime: Crime)
    @Insert
    suspend fun addCrime(crime: Crime)
    @Delete
    suspend fun deleteCrime(crime: Crime)
}