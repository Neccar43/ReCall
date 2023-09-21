package com.novacodestudios.recall.domain.model

data class QuizDetail(
    val id:Int,
    val creationDate: String,
    val correctAnswerCount:Int,
    val wrongAnswerCount:Int,
    val totalCompletionTime:Long,
){
    val successRate: String=formatSuccessRate()
    private fun calculateSuccessRate()=correctAnswerCount.toDouble()/(correctAnswerCount+wrongAnswerCount).toDouble()*100
    private fun formatSuccessRate():String=String.format("%.1f",calculateSuccessRate())
}
