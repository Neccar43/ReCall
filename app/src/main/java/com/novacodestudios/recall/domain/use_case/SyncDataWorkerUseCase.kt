package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.repository.ReCallRepository
import javax.inject.Inject

class SyncDataWorkerUseCase @Inject constructor(private val repository: ReCallRepository) {

     operator fun invoke(){
        repository.syncDataWorker()
    }
}