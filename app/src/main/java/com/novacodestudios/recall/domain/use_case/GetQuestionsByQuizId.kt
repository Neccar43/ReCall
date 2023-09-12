package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.repository.ReCallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuestionsByQuizId @Inject constructor(private val repository: ReCallRepository) {

       operator fun invoke(quizId:Int): Flow<List<Question>> {
        return repository.getQuestionsByQuizId(quizId)
    }
}