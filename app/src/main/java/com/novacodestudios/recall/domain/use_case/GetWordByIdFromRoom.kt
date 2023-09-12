package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.repository.ReCallRepository
import javax.inject.Inject

class GetWordByIdFromRoom @Inject constructor(private val repository: ReCallRepository) {

     suspend operator  fun invoke(id:Int): Word{
        return repository.getWordByIdFromRoom(id)
    }
}