package com.novacodestudios.recall.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.novacodestudios.recall.domain.model.relation.QuizWithQuestions
import com.novacodestudios.recall.util.currentISOLocaleDateTimeString
import kotlin.random.Random

@Entity
 data class Word(
    @PrimaryKey(autoGenerate = false) override val id: Int=Random.nextInt(Int.MAX_VALUE),
    val name: String,
    val meaning: String,
    val isInQuiz: Boolean = false,
    val easiness: Double = 2.5,
    val repetitions: Int = 0,
    val interval: Int = 1,
    val nextRepetitions: String = currentISOLocaleDateTimeString(),
    override val version: Long = 0L
):Synchronizable {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "meaning" to meaning,
            "isInQuiz" to isInQuiz,
            "easiness" to easiness,
            "repetitions" to repetitions,
            "interval" to interval,
            "nextRepetitions" to nextRepetitions,
            "version" to version
        )
    }
}

fun List<Word>.toQuizWithQuestions(quizId: Int = Random.nextInt(Int.MAX_VALUE)): QuizWithQuestions =
    QuizWithQuestions(
        Quiz(id = quizId),
        this.map { word ->
            Question(
                title = word.name,
                correctAnswer = word.meaning,
                quizId = quizId,
                wordId = word.id
            )
        }
    )

fun List<Word>.copyAll(isInQuiz: Boolean): List<Word> =
    map { word -> word.copy(isInQuiz = isInQuiz) }


fun QueryDocumentSnapshot.toWord(): Word {
    return Word(
        id = id.toInt(),
        name = get("name") as String,
        meaning = get("meaning") as String,
        isInQuiz = get("isInQuiz") as Boolean,
        easiness = get("easiness") as Double,
        repetitions = (get("repetitions") as Number).toInt(),
        interval = (get("interval") as Number).toInt(),
        nextRepetitions = get("nextRepetitions") as String,
        version= get("version") as Long

    )
}


