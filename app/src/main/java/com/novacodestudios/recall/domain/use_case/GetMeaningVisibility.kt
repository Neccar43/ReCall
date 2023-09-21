package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.repository.ReCallRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMeaningVisibility @Inject constructor(private val repository: ReCallRepository) {
     operator fun invoke(): Flow<Boolean> {
        return repository.getMeaningVisibility().map { it ?: false }
    }
}