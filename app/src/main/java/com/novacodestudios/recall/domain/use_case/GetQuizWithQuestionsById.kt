package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.relation.QuizWithQuestions
import com.novacodestudios.recall.domain.repository.ReCallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuizWithQuestionsById @Inject constructor(private val repository: ReCallRepository) {
    operator fun invoke(quizId:Int): Flow<QuizWithQuestions> {
        return repository.getQuizWithQuestionsById(quizId)
    }
}