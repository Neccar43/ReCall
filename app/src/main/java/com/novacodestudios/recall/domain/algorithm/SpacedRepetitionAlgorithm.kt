package com.novacodestudios.recall.domain.algorithm

import com.novacodestudios.recall.domain.model.Word
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.roundToInt

object SpacedRepetitionAlgorithm {
    fun calculateNextRepetitionDate(
        word: Word,
        isAnswerCorrect: Boolean,
        responseTime: Long,
    ): Word {
        val quality = calculateQuality(isAnswerCorrect, responseTime)
        val easiness = calculateEasinessFactor(word.easiness, quality)
        val repetitions = calculateRepetitions(quality, word.repetitions)
        val interval = calculateInterval(repetitions, word.interval, easiness)
        return word.copy(
            repetitions = repetitions,
            easiness = easiness,
            nextRepetitions = calculateNextPracticeDate(interval).toString(),
            interval = interval
        )
    }

    private fun calculateQuality(isAnswerCorrect: Boolean, responseTime: Long): Int {
        return if (isAnswerCorrect) {
            when (responseTime) {
                in 0..<2000L -> 5
                in 2000L..<4000L -> 4
                else -> 3
            }
        } else {
            when (responseTime) {
                in 0..<2000L -> 2
                in 2000L..<4000L -> 1
                else -> 0
            }
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
        val nextPracticeDate = now + Duration.ofDays(1).toMillis() * interval
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(nextPracticeDate),
            ZoneId.systemDefault()
        )
    }

}
