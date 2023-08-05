package com.novacodestudios.recall.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Int?=null,
    val question: String,
    val correctAnswer:String,
    val userAnswer:String?=null,
    val isCorrect:Boolean?=null,
    val responseTime:Long?=null,
    val quizId:Int,
    val wordId:Int?
)

