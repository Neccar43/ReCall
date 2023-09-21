package com.novacodestudios.recall.domain.repository

import com.novacodestudios.recall.domain.model.Group
import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.model.Quiz
import com.novacodestudios.recall.domain.model.Translation
import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.model.relation.QuizWithQuestions
import com.novacodestudios.recall.util.Resource
import kotlinx.coroutines.flow.Flow

interface ReCallRepository {
    fun getWordsFromRoom(): Flow<List<Word>>

    suspend fun saveWordToRoom(word: Word)

    suspend fun deleteWordFromRoom(word: Word)

    suspend fun updateWordsToRoom(words: List<Word>)

    fun getQuestionsByQuizId(quizId: Int): Flow<List<Question>>

    suspend fun updateQuestionToRoom(question: Question)

    fun getUpcomingQuizzesWithQuestions(): Flow<List<QuizWithQuestions>>

    suspend fun shouldQuizCreate(): Boolean

    fun calculateNextRepetitionDate(
        word: Word,
        isAnswerCorrect: Boolean,
        responseTime: Long,
    ): Word

    suspend fun signUpUserToFirebase(email: String, password: String)

    suspend fun signInUserWithFirebase(email: String, password: String)

    suspend fun signInUserWithGoogle()

    suspend fun signOutUser()

    suspend fun saveUserToFirestore(fullName: String, email: String)

    fun searchWords(search: String): Flow<List<Word>>

    fun getCompletedQuizzes(): Flow<List<Quiz>>

    fun getActiveQuizzes(): Flow<List<Quiz>>

    suspend fun setTheme(enableDarkTheme: Boolean)

    fun getTheme(): Flow<Boolean>

    fun getQuestionCandidateWords(): Flow<List<Word>>

    suspend fun saveQuizToRoom(quiz: Quiz)

    suspend fun saveQuestionsToRoom(questions: List<Question>)

    suspend fun updateQuizIsCompletedInRoom(quizId: Int)

    suspend fun getWordByIdFromRoom(id: Int): Word

    fun getQuizWithQuestionsById(quizId: Int): Flow<QuizWithQuestions>

    suspend fun updateQuizzesToRoom(quizzes: List<Quiz>)

    suspend fun setWordToFirestore(uid: String, word: Word)

    suspend fun setQuizToFirestore(uid: String, quiz: Quiz)

    suspend fun setQuestionToFirestore(uid: String, question: Question)

    suspend fun deleteWordFromFirestore(uid: String, word: Word)

    fun getCurrentUserUid(): String

    suspend fun updateQuizToRoom(quiz: Quiz)

    suspend fun updateWordInRoom(word: Word)

    suspend fun deleteAllWords()

    suspend fun deleteAllQuestion()

    suspend fun deleteAllQuiz()

    suspend fun getWordsFromFirestore(uid: String): List<Word>

    suspend fun getQuestionsFromFirestore(uid: String, quizId: String): List<Question>

    suspend fun getQuizzesFromFirestore(uid: String): List<Quiz>

    fun getQuizzesFromRoom(): Flow<List<Quiz>>

    fun syncDataWorker()

    suspend fun translateWord(word: String): Resource<Translation>

    fun getWordsFromRoomByGroupId(groupId: Int?): Flow<List<Word>>

    fun getGroupsFromRoom(): Flow<List<Group>>

    suspend fun updateGroupFromRoom(group: Group)

    suspend fun insertGroupFromRoom(group: Group)

    suspend fun deleteGroupFromRoom(group: Group)

    suspend fun setGroupFromFirestore(group: Group, uid: String)

    suspend fun deleteGroupFromFirestore(group: Group, uid: String)

    suspend fun getGroupsFromFirestore( uid: String):List<Group>

    suspend fun deleteQuestionFromFirestore(uid: String,question: Question)

    fun getQuestionFromActiveQuizzesByWordId(wordId:Int):Flow<List<Question>>

    suspend fun deleteQuestionFromRoom(question: Question)

    suspend fun setMeaningVisibility(meaningVisibility: Boolean)

    fun getMeaningVisibility(): Flow<Boolean?>

}