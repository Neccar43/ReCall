package com.novacodestudios.recall.domain.repository

import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.model.relation.QuizWithQuestions
import com.novacodestudios.recall.domain.model.Quiz
import com.novacodestudios.recall.domain.model.User
import com.novacodestudios.recall.domain.model.Word
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ReCallRepository {
    suspend fun getWords():Flow<List<Word>>

    suspend fun saveWord(word: Word)

    suspend fun deleteWord(word: Word)

    suspend fun updateWord(word: Word)

    suspend fun getQuiz(quizId:Int): QuizWithQuestions

    suspend fun updateQuestion(question: Question)

    suspend fun saveQuizWithQuestions(quizWithQuestions: QuizWithQuestions)

    suspend fun getQuizWithQuestionsByDate(): Flow<List<QuizWithQuestions>>

    suspend fun isTodayQuizExist(): Boolean

    fun calculateNextRepetitionDate(
        word: Word,
        isAnswerCorrect: Boolean,
        responseTime: Long,
    ): Word

    fun userSignUp(user: User)

    fun userSignIn(user: User)




}