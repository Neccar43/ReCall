package com.novacodestudios.recall.presentation.quiz_history

import com.novacodestudios.recall.domain.model.Quiz

data class QuizHistoryState(
    val pastQuizzes: List<Quiz> = emptyList(),
    val activeQuiz:Quiz?=null
    )