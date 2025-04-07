package com.example.skillcinema.data

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.inject.Inject


class DateRepository @Inject constructor() {

    private val calendar = Calendar.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    fun getFormatter(): DateTimeFormatter? {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodayDate(): String? {
        return LocalDate.now().format(getFormatter())
    }

    fun getPremierYear(): Int {
        return calendar.get(Calendar.YEAR)
    }

    fun getPremierMonth(): List<String> {
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val endOfMonth: Boolean = day > 16
        val months: List<String> = listOf(
            "JANUARY",
            "FEBRUARY",
            "MARCH",
            "APRIL",
            "MAY",
            "JUNE",
            "JULY",
            "AUGUST",
            "SEPTEMBER",
            "OCTOBER",
            "NOVEMBER",
            "DECEMBER"
        ).filterIndexed(
            if (endOfMonth) {
                { index, _ -> index == month || index == month + 1 }
            } else {
                { index, _ -> index == month }
            }
        )
        return months
    }

}


