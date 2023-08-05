package com.novacodestudios.recall.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.novacodestudios.recall.data.local.ReCallDao
import com.novacodestudios.recall.domain.algorithm.SpacedRepetitionAlgorithm
import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.model.relation.QuizWithQuestions
import com.novacodestudios.recall.domain.repository.ReCallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReCallRepositoryImpl @Inject constructor(
    private val dao: ReCallDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val algorithm:SpacedRepetitionAlgorithm
) : ReCallRepository {
    override suspend fun getWords(): Flow<List<Word>> {
        return dao.getWords()
    }

    override suspend fun saveWord(word: Word) {
        dao.insertWord(word)
    }

    override suspend fun deleteWord(word: Word) {
        dao.deleteWord(word)
    }

    override suspend fun updateWord(word: Word) {
        dao.updateWord(word)
    }

    override suspend fun getQuiz(quizId: Int): QuizWithQuestions {
        return dao.getQuizWithQuestions(quizId)
    }

    override suspend fun updateQuestion(question: Question) {
        dao.updateQuestion(question)
    }

    override suspend fun saveQuizWithQuestions(quizWithQuestions: QuizWithQuestions) {
        dao.insertQuizWithQuestions(quizWithQuestions)
    }

    override suspend fun getQuizWithQuestionsByDate(): Flow<List<QuizWithQuestions>> {
        return dao.getQuizWithQuestionsByDate()
    }

    override suspend fun isTodayQuizExist(): Boolean {
        return dao.isTodayQuizExist()
    }

    override fun calculateNextRepetitionDate(
        word: Word,
        isAnswerCorrect: Boolean,
        responseTime: Long,
    ): Word {
        return algorithm.calculateNextRepetitionDate(word,isAnswerCorrect,responseTime)
    }


}