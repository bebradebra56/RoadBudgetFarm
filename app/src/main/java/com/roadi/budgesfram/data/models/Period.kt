package com.roadi.budgesfram.data.models

import java.util.Calendar
import java.util.Date

enum class Period {
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    THIS_YEAR,
    CUSTOM;

    fun getDateRange(): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time

        return when (this) {
            TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                Pair(calendar.time, endDate)
            }
            THIS_WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                Pair(calendar.time, endDate)
            }
            THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                Pair(calendar.time, endDate)
            }
            THIS_YEAR -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                Pair(calendar.time, endDate)
            }
            CUSTOM -> {
                // For custom period, return current day as default
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                Pair(calendar.time, endDate)
            }
        }
    }

    fun getDisplayName(): String {
        return when (this) {
            TODAY -> "Today"
            THIS_WEEK -> "This Week"
            THIS_MONTH -> "This Month"
            THIS_YEAR -> "This Year"
            CUSTOM -> "Custom Period"
        }
    }
}
