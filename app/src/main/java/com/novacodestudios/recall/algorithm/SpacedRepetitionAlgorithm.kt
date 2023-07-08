package com.novacodestudios.recall.algorithm

import com.novacodestudios.recall.roomdb.table.Word
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.roundToInt

/*
1) kullanıcı kelime ekler
    val id:Int?=1,
    val originalName: String="orange",
    val translations:String="portakal",
    val nextRepetition: LocalDateTime = LocalDateTime.now(),
    val repetitions: Int = 0,
    val easinessFactor: Float = 2.5.toFloat(),
    val interval: Int = 1

    fun insertWord(word:Word)

2) Eğer o gün tekrar edilmesi gereken kelimeler varsa 10 soruluk quiz hazırlanır
    @Query("SELECT * FROM Word WHERE :now>=nextRepetition")
    fun getWordsForQuiz(now: LocalDateTime = LocalDateTime.now()):List<Word>

3) Bunların arasından önceliği en küçük tarihliye göre 10 soru seçilir


* */

class SpacedRepetitionAlgorithm {

    /*fun calculateNextRepetitionDate(word: Word, quality: Int): Word {
        validateQualityFactorInput(quality)

        val easiness = calculateEasinessFactor(word.easinessFactor, quality)
        val repetitions = calculateRepetitions(quality, word.repetitions)
        val interval = calculateInterval(repetitions, word.interval, easiness)
        return word.withUpdatedRepetitionProperties(
            newRepetitions = repetitions,
            newEasinessFactor = easiness,
            newNextRepetitionDate = calculateNextPracticeDate(interval),
            newInterval = interval
        )
    }*/

    private fun validateQualityFactorInput(quality: Int) {
        if (quality < 0 || quality > 5) {
            throw IllegalArgumentException("Provided quality value is invalid ($quality)")
        }
    }

    private fun calculateEasinessFactor(easiness: Float, quality: Int) =
        Math.max(1.3, easiness + 0.1 - (5.0 - quality) * (0.08 + (5.0 - quality) * 0.02)).toFloat()

    private fun calculateRepetitions(quality: Int, wordRepetitions: Int) = if (quality < 3) {
        0
    } else {
        wordRepetitions + 1
    }

    private fun calculateInterval(repetitions: Int, wordInterval: Int, easiness: Float) = when {
        repetitions <= 1 -> 1
        repetitions == 2 -> 6
        else -> (wordInterval * easiness).roundToInt()
    }

    private fun calculateNextPracticeDate(interval: Int): LocalDateTime {
        val now = System.currentTimeMillis()
        val nextPracticeDate = now + dayInMs * interval
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(nextPracticeDate),
            ZoneId.systemDefault()
        )
    }


    private companion object {
        private val dayInMs = Duration.ofDays(1).toMillis()
    }
}
