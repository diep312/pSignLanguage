package com.ptit.signlanguage.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration


class DateUtils {
    companion object {
        @JvmStatic
        fun convertISO8601ToSeconds(input : String) : Long {
            return Duration.parse(input).inWholeSeconds
        }

        @JvmStatic
        fun formatSeconds(timeInSeconds: Long): String? {
            val hours = timeInSeconds / 3600
            val secondsLeft = timeInSeconds - hours * 3600
            val minutes = secondsLeft / 60
            val seconds = secondsLeft - minutes * 60
            var formattedTime = ""
            if (hours in 1L..9) {
                formattedTime += "0"
            }
            if(hours != 0L) {
                formattedTime += "$hours:"
            }

            if (minutes < 10) formattedTime += "0"
            formattedTime += "$minutes:"
            if (seconds < 10) formattedTime += "0"
            formattedTime += seconds
            return formattedTime
        }

        fun convertDateToString(date: Date, format: String): String? {
            return try {
                val sdf = SimpleDateFormat(format)
                sdf.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
                null
            }
        }

        fun convertStringToDate(dateStr: String, format: String): Date? {
            return try {
                SimpleDateFormat(format).parse(dateStr)
            } catch (e: ParseException) {
                e.printStackTrace()
                null
            }
        }
    }
}