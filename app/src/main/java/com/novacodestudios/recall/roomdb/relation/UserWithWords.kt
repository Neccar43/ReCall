package com.novacodestudios.recall.roomdb.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.novacodestudios.recall.roomdb.table.User
import com.novacodestudios.recall.roomdb.table.Word

data class UserWithWords(
    @Embedded val user:User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val words:List<Word>
)
