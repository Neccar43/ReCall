package com.novacodestudios.recall.presentation.quiz_history

import com.novacodestudios.recall.domain.model.Quiz
import com.novacodestudios.recall.domain.model.QuizDetail

data class QuizHistoryState(
    val pastQuizzes: List<QuizDetail> = emptyList(),
    val activeQuiz:Quiz?=null
    )