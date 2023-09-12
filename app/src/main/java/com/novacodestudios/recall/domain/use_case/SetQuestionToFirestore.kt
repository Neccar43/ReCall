package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.repository.ReCallRepository
import javax.inject.Inject

class SetQuestionToFirestore @Inject constructor(
    private val repository: ReCallRepository,
) {
    suspend operator fun invoke(question: Question) {
        val uid=repository.getCurrentUserUid()
        repository.setQuestionToFirestore(uid,question)
    }
}