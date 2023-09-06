package com.novacodestudios.recall.presentation.question

sealed class QuestionEvent{
    data class SubmitAnswer(val answer:String, val responseTime:Long):QuestionEvent()
    data class AnswerChange(val answer:String):QuestionEvent()
    data object CancelQuiz:QuestionEvent()
    data object StartTime:QuestionEvent()
    data object FinishQuiz:QuestionEvent()
}
