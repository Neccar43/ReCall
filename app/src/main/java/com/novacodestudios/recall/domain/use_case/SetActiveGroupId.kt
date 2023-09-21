package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.repository.ReCallRepository
import javax.inject.Inject

class SetActiveGroupId @Inject constructor(private val repository: ReCallRepository) {
     suspend operator fun invoke(groupId:Int){
         repository.setActiveGroupId(groupId)
    }
}