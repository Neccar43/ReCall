package com.novacodestudios.recall.util

import android.content.Context
import com.novacodestudios.recall.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun Any?.isNotNull():Boolean{
    return this!=null
}

 fun <R>List<R>.updateElementByIndex(index:Int, newElement:R):List<R> {
    if (index < 0 || index >= size) {
        throw IndexOutOfBoundsException()
    }
    val updatedList = ArrayList(this)
    updatedList[index] = newElement

    return updatedList.toList()
}
fun relativeTimeAgo(dateTimeString: String, context: Context): String {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val givenDateTime = LocalDateTime.parse(dateTimeString, formatter)
    val currentDateTime = LocalDateTime.now()

    //iki tarih arasındaki farkı belirlenen zaman türünden verir.
    val difference = ChronoUnit.SECONDS.between(givenDateTime, currentDateTime)

    val secondsInMinute = 60
    val secondsInHour = secondsInMinute * 60
    val secondsInDay = secondsInHour * 24
    val secondsInWeek = secondsInDay * 7
    val secondsInMonth = secondsInDay * 30

    return when {
        difference < secondsInMinute -> context.getString(R.string.now)
        difference < secondsInHour -> "${difference / secondsInMinute} ${context.getString(R.string.minute_ago)}"
        difference < secondsInDay -> "${difference / secondsInHour} ${context.getString(R.string.hour_ago)}"
        difference < secondsInWeek -> "${difference / secondsInDay} ${context.getString(R.string.day_ago)}"
        difference < secondsInMonth -> "${difference / secondsInWeek} ${context.getString(R.string.week_ago)}"
        else -> "${difference / secondsInMonth} ${context.getString(R.string.month_ago)}"
    }

}

fun currentISOLocaleDateTimeString(): String {
    val currentDateTime = LocalDateTime.now()
    return currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

fun formatTime(milliSec:Long):String{
    val second=milliSec/1000
    val minute=second/60
    val remainSecond=second%60
    return when{
        minute>0 && remainSecond>0->"$minute dk $remainSecond sn"
        else-> "$remainSecond sn"
    }
}
