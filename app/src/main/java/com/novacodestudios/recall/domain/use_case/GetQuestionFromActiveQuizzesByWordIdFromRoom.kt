package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.repository.ReCallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuestionFromActiveQuizzesByWordIdFromRoom @Inject constructor(private val repository: ReCallRepository) {

      operator  fun invoke(wordId:Int): Flow<List<Question>> {
        return repository.getQuestionFromActiveQuizzesByWordId(wordId)
    }
}