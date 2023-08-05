package com.novacodestudios.recall.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    //id leri firebase den alacağız
    @PrimaryKey(autoGenerate = false)
    val id: Int? = null,
    val name:String,
    val surname:String,
    val email:String,
)



