package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.repository.ReCallRepository
import javax.inject.Inject

class SetMeaningVisibility @Inject constructor(private val repository: ReCallRepository) {
    suspend operator fun invoke(meaningVisibility:Boolean){
        repository.setMeaningVisibility(meaningVisibility)
    }
}