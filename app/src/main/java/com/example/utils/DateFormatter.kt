package com.example.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateFormatter {

    private fun isoFormatter() = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    /** Today in the app-wide `yyyy-MM-dd` convention. */
    fun today(): String = isoFormatter().format(Date())

    /** The date [days] days from today (negative = in the past), as `yyyy-MM-dd`. */
    fun daysFromToday(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return isoFormatter().format(cal.time)
    }

    fun formatIsoDateToHumanReadable(isoDateStr: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = parser.parse(isoDateStr)
            if (date != null) {
                val formatter = SimpleDateFormat("EEEE, MMMM d", Locale.US)
                formatter.format(date)
            } else {
                isoDateStr
            }
        } catch (e: Exception) {
            isoDateStr
        }
    }
}
