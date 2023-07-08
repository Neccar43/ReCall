package com.novacodestudios.recall.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.novacodestudios.recall.roomdb.relation.QuizWithQuizWords
import com.novacodestudios.recall.roomdb.relation.UserWithQuizzes
import com.novacodestudios.recall.roomdb.relation.UserWithWords
import com.novacodestudios.recall.roomdb.table.Quiz
import com.novacodestudios.recall.roomdb.table.QuizWord
import com.novacodestudios.recall.roomdb.table.User
import com.novacodestudios.recall.roomdb.table.Word
import java.time.LocalDateTime

@Dao
interface ReCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: Quiz)

    @Delete
    suspend fun deleteQuiz(quiz: Quiz)

    @Insert
    suspend fun insertQuizWord(quizWord:QuizWord)

    @Delete
    suspend fun deleteQuizWord(quizWord:QuizWord)

/*    @Query("SELECT * FROM Quiz WHERE date <= :now")
    suspend fun getQuizByDate(now: LocalDateTime = LocalDateTime.now()):List<Quiz>

    @Query("SELECT id  FROM Word WHERE :now>=nextRepetition ORDER BY nextRepetition ASC")
    suspend fun getWordsForQuiz(now: LocalDateTime = LocalDateTime.now()): List<Word>

    //Bildirim göndermek için kullanılacak 24 saatte bir çalıştırılacak.
    @Query("SELECT COUNT(*) >= 10 FROM Word WHERE nextRepetition <= :now")
    suspend fun isTodayQuizExist(now: LocalDateTime = LocalDateTime.now()): Boolean*/

    @Transaction
    @Query("SELECT * FROM Quiz WHERE id=:quizId")
    suspend fun getQuizWithQuizWords(quizId: Int): QuizWithQuizWords

    @Transaction
    @Query("SELECT * FROM User WHERE id=:userId")
    suspend fun getUserWithQuizzes(userId: Int): UserWithQuizzes

    @Transaction
    @Query("SELECT * FROM User WHERE id=:userId")
    suspend fun getUserWithWords(userId: Int): UserWithWords


}