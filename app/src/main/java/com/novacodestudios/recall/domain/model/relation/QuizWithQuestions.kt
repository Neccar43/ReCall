package com.novacodestudios.recall.domain.model.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.model.Quiz
import com.novacodestudios.recall.domain.model.QuizDetail

data class QuizWithQuestions(
    @Embedded val quiz: Quiz,
    @Relation(
        parentColumn = "id",
        entityColumn = "quizId"
    )
    val questions:List<Question>
){
    fun toQuizDetail():QuizDetail {
        val correctCount=questions.count{it.userAnswer==it.correctAnswer}
        val totalTime=questions.sumOf { it.responseTime!! }
        return QuizDetail(
            id = quiz.id,
            creationDate = quiz.date,
            correctAnswerCount =correctCount ,
            wrongAnswerCount =questions.size-correctCount,
            totalCompletionTime = totalTime
        )
    }
}