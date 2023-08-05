package com.novacodestudios.recall.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.model.Quiz
import com.novacodestudios.recall.domain.model.User
import com.novacodestudios.recall.domain.model.Word

@Database(entities = [Word::class,Question::class,Quiz::class,User::class,], version = 2)
abstract class ReCallDatabase:RoomDatabase() {
    abstract fun reCallDao(): ReCallDao
}