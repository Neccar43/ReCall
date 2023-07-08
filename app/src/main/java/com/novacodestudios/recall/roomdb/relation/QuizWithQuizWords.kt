package com.novacodestudios.recall.roomdb.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.novacodestudios.recall.roomdb.table.Quiz
import com.novacodestudios.recall.roomdb.table.QuizWord


data class QuizWithQuizWords(
    @Embedded
    val quiz: Quiz,
    @Relation(
        parentColumn = "id",
        entityColumn = "quizId"
    )
    val quizWords:List<QuizWord>
)
