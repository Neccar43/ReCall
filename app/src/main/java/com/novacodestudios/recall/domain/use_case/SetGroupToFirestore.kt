package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.Group
import com.novacodestudios.recall.domain.repository.ReCallRepository
import javax.inject.Inject

class SetGroupToFirestore @Inject constructor(private val repository: ReCallRepository) {

       suspend operator fun invoke(group: Group){
           val uid=repository.getCurrentUserUid()
        return repository.setGroupFromFirestore(group,uid)
    }
}