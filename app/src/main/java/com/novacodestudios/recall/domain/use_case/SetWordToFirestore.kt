package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.repository.ReCallRepository
import javax.inject.Inject

class SetWordToFirestore @Inject constructor(
    private val repository: ReCallRepository,
) {
    suspend operator fun invoke(word: Word) {
        val uid=repository.getCurrentUserUid()
        repository.setWordToFirestore(uid,word)
    }
}