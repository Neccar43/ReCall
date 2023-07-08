package com.novacodestudios.recall.roomdb.table

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Quiz(
    @PrimaryKey(autoGenerate = true)
    val id:Int?=null,
    val date: String = LocalDateTime.now().toString(),
    val userId:Int
)
