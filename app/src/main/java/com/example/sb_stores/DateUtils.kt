package com.example.sb_stores

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.*

class DateUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodaysDate(): String{
        val current_date = LocalDate.now()
        val date = "${
            String.format(
                "%02d",
                current_date.dayOfMonth
            )
        }-${String.format("%02d", current_date.monthValue)}-${current_date.year}"

        return date
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate(date: LocalDate): String{
        val current_date =date
        val date = "${
            String.format(
                "%02d",
                current_date.dayOfMonth
            )
        }-${String.format("%02d", current_date.monthValue)}-${current_date.year}"

        return date
    }

    fun isLeap(year: Int): Boolean{


//        # To get year (integer input) from the user
//        # year = int(input("Enter a year: "))
//
//        # divided by 100 means century year (ending with 00)
//        # century year divided by 400 is leap year
        if (year % 400 == 0 && year % 100 == 0){
            print("{0} is a leap year".format(year))
            return true
        }

//        # not divided by 100 means not a century year
//        # year divided by 4 is a leap year
        else if (year % 4 ==0 && year % 100 != 0) {
            print("{0} is a leap year".format(year))
            return true

        }

//        # if not divided by both 400 (century year) and 4 (not century year)
//        # year is not leap year
        else {
            print("{0} is not a leap year".format(year))
            return false
        }
    }

    fun toDate(date: String): Date? {
        val format = SimpleDateFormat("dd-MM-yyyy")
        return format.parse(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toLocalDate(date: String): LocalDate? {
        val d = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        return d
    }
}