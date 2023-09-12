package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.repository.ReCallRepository
import javax.inject.Inject

class DeleteWordFromFirestore @Inject constructor(private val repository: ReCallRepository) {

     suspend operator fun invoke(word: Word){
         val uid=repository.getCurrentUserUid()
        repository.deleteWordFromFirestore(uid,word)
    }
}