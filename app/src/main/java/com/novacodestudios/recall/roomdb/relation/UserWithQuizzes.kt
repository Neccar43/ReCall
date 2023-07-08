package com.novacodestudios.recall.roomdb.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.novacodestudios.recall.roomdb.table.Quiz
import com.novacodestudios.recall.roomdb.table.User

data class UserWithQuizzes(
    @Embedded
    val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val quizzes:List<Quiz>
)
