package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.Translation
import com.novacodestudios.recall.domain.repository.ReCallRepository
import com.novacodestudios.recall.util.Resource
import javax.inject.Inject

class TranslateWord @Inject constructor(private val repository: ReCallRepository) {

    suspend operator fun invoke(word:String): Resource<Translation> {
        return repository.translateWord(word)
    }
}