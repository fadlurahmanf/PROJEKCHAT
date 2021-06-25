package com.example.projekchat.utils

import java.util.*

object TimeConvert {
    fun timestampToDay(timestamp:Long): String {
        var now = System.currentTimeMillis()
        var nowCalender = Calendar.getInstance()
        nowCalender.timeInMillis = now
        var nowDay = nowCalender.get(Calendar.DAY_OF_WEEK)

        var calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        var day = calendar.get(Calendar.DAY_OF_WEEK)
        var minute = calendar.get(Calendar.MINUTE).toString()
        var hour = calendar.get(Calendar.HOUR_OF_DAY).toString()

        var time:String = ""

        if (nowDay==day){
            if (hour.length==1){
                hour = "0$hour"
            }
            if (minute.length==1){
                minute = "0$minute"
            }
            time = "$hour:$minute"
        }else{
            if (day==1){
                time = "SUN"
            }else if (day==2){
                time = "MON"
            }else if (day==3){
                time = "TUE"
            }else if (day==4){
                time = "WED"
            }else if (day==5){
                time = "THU"
            }else if (day==6){
                time = "FRI"
            }else if (day==7){
                time = "SAT"
            }
        }
        return time
    }
}