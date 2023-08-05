package com.novacodestudios.recall.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.novacodestudios.recall.domain.model.relation.QuizWithQuestions
import java.time.LocalDateTime

@Entity
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val meaning: String,
    val easiness: Float = 2.5.toFloat(),
    val repetitions: Int = 0,
    val interval: Int = 1,
    val nextRepetitions: String = LocalDateTime.now().toString(),
    val userId: Int,
) {
    fun toQuestion(quizId: Int): Question {
        return Question(
            question = name,
            correctAnswer = meaning,
            quizId = quizId,
            wordId = id
        )
    }
}





