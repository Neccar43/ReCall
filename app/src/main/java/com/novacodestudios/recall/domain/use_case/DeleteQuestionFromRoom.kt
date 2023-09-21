package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.repository.ReCallRepository
import javax.inject.Inject

class DeleteQuestionFromRoom @Inject constructor(private val repository: ReCallRepository) {

      suspend operator  fun invoke(question: Question){
        return repository.deleteQuestionFromRoom(question)
    }
}