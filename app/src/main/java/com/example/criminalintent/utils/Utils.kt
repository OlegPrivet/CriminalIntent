package com.example.criminalintent.utils

import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object{
        fun dateFormat(date: Date) : String{
            return SimpleDateFormat("EEEE, HH:mm d MMMM  yyyy", Locale.ENGLISH).format(date).toString()
        }
    }

}