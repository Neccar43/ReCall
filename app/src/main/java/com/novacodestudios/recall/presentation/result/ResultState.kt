package com.novacodestudios.recall.presentation.result

import com.novacodestudios.recall.domain.model.QuizDetail
import com.novacodestudios.recall.domain.model.relation.QuizWithQuestions

data class ResultState(
    val quizDetail:QuizDetail?=null,
    val quizWithQuestions: QuizWithQuestions?=null



)
