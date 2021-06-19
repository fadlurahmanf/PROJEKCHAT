package com.example.projekchat.utils

import java.util.*

object TimeConvert {
    fun timestampToDay(timestamp:Long): String {
        var calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        var day = calendar.get(Calendar.DAY_OF_WEEK)
        return when(day){
            1-> "SUN"
            2-> "MON"
            3-> "TUE"
            4-> "WED"
            5-> "THU"
            6-> "FRI"
            7-> "SAT"
            else -> ""
        }
    }
}