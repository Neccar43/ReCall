package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.Quiz
import com.novacodestudios.recall.domain.repository.ReCallRepository
import javax.inject.Inject

class SetQuizToFirestore @Inject constructor(
    private val repository: ReCallRepository,
) {
    suspend operator fun invoke(quiz: Quiz) {
        val uid=repository.getCurrentUserUid()
        repository.setQuizToFirestore(uid,quiz)
    }
}