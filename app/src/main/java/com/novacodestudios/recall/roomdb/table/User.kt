package com.novacodestudios.recall.roomdb.table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    //firebase'in oluşturduğu id yi kullanacağız
    @PrimaryKey(autoGenerate = false)
    val id:Int?=null,
    val name:String,
    val surname:String,
    val email:String,
)
