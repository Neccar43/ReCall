package com.novacodestudios.recall.domain.use_case

import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.repository.ReCallRepository
import com.novacodestudios.recall.domain.util.OrderType
import com.novacodestudios.recall.domain.util.WordOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetWordsFromRoomByGroupId @Inject constructor(private val repository: ReCallRepository) {

    operator fun invoke(wordOrder: WordOrder = WordOrder.CreationDate(OrderType.Descending),groupId:Int?): Flow<List<Word>> {
        return repository.getWordsFromRoomByGroupId(groupId).map { words ->
            when (wordOrder.orderType) {
                OrderType.Ascending -> {
                    when (wordOrder) {
                        is WordOrder.Alphabetically -> words.sortedBy { it.name }
                        is WordOrder.CreationDate -> words.sortedBy { it.creationDate }
                    }
                }
                OrderType.Descending -> {
                    when (wordOrder) {
                        is WordOrder.Alphabetically -> words.sortedByDescending { it.name }
                        is WordOrder.CreationDate -> words.sortedByDescending { it.creationDate }
                    }
                }
            }
        }
    }
}