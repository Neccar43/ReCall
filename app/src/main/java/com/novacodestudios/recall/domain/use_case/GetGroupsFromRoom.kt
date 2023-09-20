package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.Group
import com.novacodestudios.recall.domain.repository.ReCallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupsFromRoom @Inject constructor(private val repository: ReCallRepository) {

      operator  fun invoke(): Flow<List<Group>> {
        return repository.getGroupsFromRoom()
    }
}