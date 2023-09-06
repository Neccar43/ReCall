package com.novacodestudios.recall.presentation.result

import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.model.Quiz

data class ResultState(
    val quiz: Quiz?=null,
    val questions: List<Question> = emptyList(),

){
    val wrongAnswerCount by lazy { questions.count { it.userAnswer!=it.correctAnswer } }
    val correctAnswerCount by lazy {questions.count { it.userAnswer==it.correctAnswer }  }
}
