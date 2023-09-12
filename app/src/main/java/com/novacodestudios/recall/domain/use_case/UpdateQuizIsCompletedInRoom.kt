package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.repository.ReCallRepository
import javax.inject.Inject

class UpdateQuizIsCompletedInRoom @Inject constructor(private val repository: ReCallRepository) {

     suspend operator fun invoke(quizId:Int) {
        repository.updateQuizIsCompletedInRoom(quizId)
    }
}