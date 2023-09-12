package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.repository.ReCallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWordsFromRoom @Inject constructor(private val repository: ReCallRepository) {

     operator fun invoke():Flow<List<Word>>{
        return repository.getWordsFromRoom()
    }
}