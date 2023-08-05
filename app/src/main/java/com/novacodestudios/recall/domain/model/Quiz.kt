package com.novacodestudios.recall.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Quiz(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val userId:Int,
    val date: String=LocalDateTime.now().toString()
)