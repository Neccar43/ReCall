package com.novacodestudios.recall.domain.model.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.novacodestudios.recall.domain.model.Quiz
import com.novacodestudios.recall.domain.model.User

data class UserWithQuizzes(
    @Embedded
    val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val quizzes: List<Quiz>
)