package com.novacodestudios.recall.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.novacodestudios.recall.roomdb.table.Quiz
import com.novacodestudios.recall.roomdb.table.QuizWord
import com.novacodestudios.recall.roomdb.table.User
import com.novacodestudios.recall.roomdb.table.Word

@Database(entities = [User::class,Word::class,Quiz::class,QuizWord::class], version = 1)
abstract class ReCallDatabase:RoomDatabase() {
    abstract fun reCallDao():ReCallDao
}