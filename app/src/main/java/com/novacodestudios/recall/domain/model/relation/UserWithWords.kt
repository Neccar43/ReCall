package com.novacodestudios.recall.domain.model.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.novacodestudios.recall.domain.model.User
import com.novacodestudios.recall.domain.model.Word

data class UserWithWords(
    @Embedded
    val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val words:List<Word>
)