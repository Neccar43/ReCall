package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.repository.ReCallRepository
import javax.inject.Inject

class CalculateNextRepetitionDate @Inject constructor(private val repository: ReCallRepository) {

    operator fun invoke(
        word: Word,
        isAnswerCorrect: Boolean,
        responseTime: Long,
    ): Word {
        return repository.calculateNextRepetitionDate(word, isAnswerCorrect, responseTime)
    }
}