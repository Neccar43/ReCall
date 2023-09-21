package com.novacodestudios.recall.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlin.random.Random

@Entity
data class Question(
    @PrimaryKey(autoGenerate = true) override val id: Int= Random.nextInt(Int.MAX_VALUE),
    val title: String,
    val correctAnswer: String,
    val userAnswer: String? = null,
    val responseTime: Long? = null,
    val quizId: Int,
    val wordId: Int?,
    override val version: Long = 0L
) :Synchronizable{
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "correctAnswer" to correctAnswer,
            "userAnswer" to userAnswer,
            "responseTime" to responseTime,
            "quizId" to quizId,
            "wordId" to wordId,
            "version" to version
        )
    }
}

fun QueryDocumentSnapshot.toQuestion(): Question {
    return Question(
        id = id.toInt(),
        title = get("title") as String,
        correctAnswer = get("correctAnswer") as String,
        userAnswer = get("userAnswer") as String?,
        responseTime = get("responseTime") as Long?,
        quizId = (get("quizId") as Number).toInt(),
        wordId = (get("wordId") as Number).toInt(),
        version = get("version") as Long
    )
}
