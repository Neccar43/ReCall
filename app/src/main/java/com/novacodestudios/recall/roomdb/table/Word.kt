package com.novacodestudios.recall.roomdb.table

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id:Int?=null,
    val originalName: String,
    val translations:String,
    val nextRepetition: String = LocalDateTime.now().toString(),
    val repetitions: Int = 0,
    val easinessFactor: Float = 2.5.toFloat(),
    val interval: Int = 1,
    val userId:Int
    ){
    /*fun withUpdatedRepetitionProperties(
        newRepetitions: Int,
        newEasinessFactor: Float,
        newNextRepetitionDate: LocalDateTime,
        newInterval: Int
    ) = copy(
        repetitions = newRepetitions,
        easinessFactor = newEasinessFactor,
        nextRepetition = newNextRepetitionDate,
        interval = newInterval
    )*/

}

