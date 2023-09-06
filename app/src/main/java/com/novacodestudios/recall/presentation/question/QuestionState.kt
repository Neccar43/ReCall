package com.novacodestudios.recall.presentation.question

import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.model.Quiz

data class QuestionState(
    val questions: List<Question> = emptyList(),
    val answer:String="",
    val questionIndex:Int=0,
    val startTime:Long=0L,
    val quiz: Quiz?=null

    )
