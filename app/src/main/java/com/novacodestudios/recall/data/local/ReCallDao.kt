package com.novacodestudios.recall.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.model.relation.QuizWithQuestions
import com.novacodestudios.recall.domain.model.relation.UserWithQuizzes
import com.novacodestudios.recall.domain.model.relation.UserWithWords
import com.novacodestudios.recall.domain.model.Quiz
import com.novacodestudios.recall.domain.model.Word
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ReCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)

    @Query("SELECT * FROM Word")
    suspend fun getWords(): Flow<List<Word>>

    @Update
    suspend fun updateWord(word: Word)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizWithQuestions(quizWithQuestions: QuizWithQuestions)

    @Transaction
    @Query("SELECT * FROM Quiz WHERE :quizId=id")
    suspend fun getQuizWithQuestions(quizId:Int): QuizWithQuestions

    @Query("SELECT id  FROM Word WHERE :now>=nextRepetitions ORDER BY nextRepetitions ASC")
    suspend fun getWordsByDate(now: String = LocalDateTime.now().toString()): Flow<List<Word>>

    @Transaction
    @Query("SELECT * FROM Quiz WHERE date <= :now ORDER BY date ASC")
    suspend fun getQuizWithQuestionsByDate(now: String = LocalDateTime.now().toString()): Flow<List<QuizWithQuestions>>

    //Bildirim göndermek için kullanılacak 24 saatte bir çalıştırılacak.
    @Query("SELECT COUNT(*) >= 10 FROM Word WHERE nextRepetitions <= :now")
    suspend fun isTodayQuizExist(now: String = LocalDateTime.now().toString()): Boolean


    @Transaction
    @Query("SELECT * FROM User WHERE id=:userId")
    suspend fun getUserWithQuizzes(userId: Int): UserWithQuizzes

    @Transaction
    @Query("SELECT * FROM User WHERE id=:userId")
    suspend fun getUserWithWords(userId: Int): UserWithWords


    @Update
    suspend fun updateQuestion(question: Question)

}