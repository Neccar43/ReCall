package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.repository.ReCallRepository
import javax.inject.Inject

class DeleteAllQuizzesInFirestore @Inject constructor(private val repository: ReCallRepository) {

     suspend operator fun invoke(){
         val uid=repository.getCurrentUserUid()
        repository.deleteAllQuizzesInFirestore(uid)
    }
}