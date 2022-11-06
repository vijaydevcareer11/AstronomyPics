package com.vmobile.astronomypics.utils

import java.util.*

object DateUtils {

    /**
     * Utility method to get current date
     *
     * @return [String]
     */
    fun getCurrentDate(): String {
        var day: String = ""
        val calendar = Calendar.getInstance()

        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        day = if (currentDay < 9) {
            "0$currentDay"
        } else {
            currentDay.toString()
        }
        return currentYear.toString() + "-" + (currentMonth).toString() + "-" + day
    }

    /**
     * Convert the date, month and year into yyyy-mm-dd format
     *
     * @param currentYear
     * @param currentMonth
     * @param currentDay
     * @return
     */
    fun getSelectedDate(currentYear: Int, currentMonth: Int, currentDay: Int): String {
        var day: String = ""
        day = if (currentDay < 9) {
            "0$currentDay"
        } else {
            currentDay.toString()
        }
        val newDate = currentYear.toString() + "-" + (currentMonth + 1).toString() + "-" + day
        return newDate;
    }
}