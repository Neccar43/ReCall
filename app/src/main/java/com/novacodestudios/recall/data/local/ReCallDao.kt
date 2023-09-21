package com.novacodestudios.recall.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.novacodestudios.recall.domain.model.Group
import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.model.Quiz
import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.model.relation.QuizWithQuestions
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ReCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordToRoom(word: Word)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizToRoom(quiz: Quiz)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(vararg questions: Question)

    @Update
    suspend fun updateWordsInRoom(vararg words: Word)

    @Update
    suspend fun updateWord(word: Word)

    @Update
    suspend fun updateQuestionInRoom(question: Question)

    @Update
    suspend fun updateQuizzes(vararg quizzes: Quiz)

    @Update
    suspend fun updateQuiz(quiz: Quiz)

    @Delete
    suspend fun deleteWordFromRoom(word: Word)

    @Query("DELETE FROM Word")
    suspend fun deleteAllWords()

    @Query("DELETE FROM Question")
    suspend fun deleteAllQuestion()

    @Query("DELETE FROM Quiz")
    suspend fun deleteAllQuizzes()

    @Query("SELECT * FROM Word")
    fun getAllWordsFromRoom(): Flow<List<Word>>

    @Query("SELECT * FROM Word WHERE groupId=:groupId")
    fun getWordsByGroupId(groupId:Int):Flow<List<Word>>

    @Query("SELECT * FROM Quiz")
    fun getAllQuizzesFromRoom(): Flow<List<Quiz>>

    @Query(
        """
         SELECT * FROM Word 
         WHERE LOWER(name) LIKE '%' || LOWER(:search) || '%' OR 
         LOWER(meaning) LIKE '%' || LOWER(:search) || '%' 
         """
    )
    fun searchWords(search: String): Flow<List<Word>>

    @Query("SELECT * FROM Question WHERE quizId=:quizId")
    fun getQuestionsByQuizId(quizId: Int): Flow<List<Question>>

    @Query("SELECT * FROM Quiz WHERE isCompleted=1 ORDER BY date DESC")
    fun getCompletedQuizzesFromRoom(): Flow<List<Quiz>>

    @Query("SELECT * FROM Quiz WHERE isCompleted=0 ORDER BY date")
    fun getActiveQuizzesFromRoom(): Flow<List<Quiz>>

    @Query("SELECT *  FROM Word WHERE :now>=nextRepetitions AND isInQuiz=0 ORDER BY nextRepetitions ASC")
    fun getQuestionCandidateWords(now: String = LocalDateTime.now().toString()): Flow<List<Word>>

    @Transaction
    @Query("SELECT * FROM Quiz WHERE date <= :now AND isCompleted=0 ORDER BY date ASC")
    fun getUpcomingQuizzesWithQuestions(
        now: String = LocalDateTime.now().toString()
    ): Flow<List<QuizWithQuestions>>

    @Query("SELECT * FROM Word WHERE id=:id")
    suspend fun getWordById(id: Int): Word

    @Transaction
    @Query("SELECT * FROM Quiz WHERE id=:quizId")
    fun getQuizWithQuestionsByQuizId(quizId: Int): Flow<QuizWithQuestions>

    @Query("SELECT COUNT(*) >= 10 FROM Word WHERE nextRepetitions <= :now AND isInQuiz=0")
    suspend fun shouldCreateQuiz(now: String = LocalDateTime.now().toString()): Boolean

    @Query("UPDATE Quiz SET isCompleted=1 WHERE id=:quizId")
    suspend fun updateQuizIsCompleted(quizId: Int)

    @Query("SELECT * FROM `Group`")
    fun getGroups():Flow<List<Group>>

    @Update
    suspend fun updateGroup(group: Group)

    @Insert
    suspend fun insertGroup(group: Group)

    @Delete
    suspend fun deleteGroup(group: Group)

    @Query("SELECT * FROM Question WHERE userAnswer IS NULL AND wordId=:wordId")
     fun getQuestionFromActiveQuizzesByWordId(wordId:Int):Flow<List<Question>>

     @Delete
     suspend fun deleteQuestion(question: Question)


}