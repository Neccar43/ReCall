package com.novacodestudios.recall.domain.model.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.model.Quiz

data class QuizWithQuestions(
    @Embedded val quiz: Quiz,
    @Relation(
        parentColumn = "id",
        entityColumn = "quizId"
    )
    val questions:List<Question>
)