package com.example.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.criminalintent.database.dao.CrimeDao
import com.example.criminalintent.models.Crime

@Database(entities = [Crime::class], version = 1)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDB : RoomDatabase() {

    abstract fun crimeDao(): CrimeDao

}