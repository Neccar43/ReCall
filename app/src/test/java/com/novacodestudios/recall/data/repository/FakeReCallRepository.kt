package com.novacodestudios.recall.data.repository

import com.novacodestudios.recall.domain.model.Group
import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.model.Quiz
import com.novacodestudios.recall.domain.model.Translation
import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.model.relation.QuizWithQuestions
import com.novacodestudios.recall.domain.repository.ReCallRepository
import com.novacodestudios.recall.util.Resource
import com.novacodestudios.recall.util.updateElementByIndex
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeReCallRepository : ReCallRepository {
    private val wordsFromRoom = mutableListOf<Word>()
    private val quizzesFromRoom = mutableListOf<Quiz>()
    private val questionsFromRoom = mutableListOf<Question>()
    private val quizWithQuestions= mutableListOf<QuizWithQuestions>()

    override fun getWordsFromRoom(): Flow<List<Word>> {
        return flow { emit(wordsFromRoom) }
    }

    override suspend fun saveWordToRoom(word: Word) {
        wordsFromRoom.add(word)
    }

    override suspend fun deleteWordFromRoom(word: Word) {
        wordsFromRoom.remove(word)
    }

    override suspend fun updateWordsToRoom(words: List<Word>) {
        words.forEachIndexed { index, word -> wordsFromRoom.updateElementByIndex(index, word) }
    }

    override fun getQuestionsByQuizId(quizId: Int): Flow<List<Question>> {
       val foundQuestions= questionsFromRoom.filter { quizId==it.quizId }
        return flow { emit(foundQuestions) }
    }

    override suspend fun updateQuestionToRoom(question: Question) {
        questionsFromRoom.remove(question)
        questionsFromRoom.add(question)
    }

    override fun getUpcomingQuizzesWithQuestions(): Flow<List<QuizWithQuestions>> {
        return flow {  }
    }

    override suspend fun shouldQuizCreate(): Boolean {
        TODO("Not yet implemented")
    }

    override fun calculateNextRepetitionDate(
        word: Word,
        isAnswerCorrect: Boolean,
        responseTime: Long
    ): Word {
        TODO("Not yet implemented")
    }

    override suspend fun signUpUserToFirebase(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun signInUserWithFirebase(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun signInUserWithGoogle() {
        TODO("Not yet implemented")
    }

    override suspend fun signOutUser() {
        TODO("Not yet implemented")
    }

    override suspend fun saveUserToFirestore(fullName: String, email: String) {
        TODO("Not yet implemented")
    }

    override fun searchWords(search: String, groupId: Int?): Flow<List<Word>> {
        TODO("Not yet implemented")
    }

    override fun getCompletedQuizzes(): Flow<List<Quiz>> {
        TODO("Not yet implemented")
    }

    override fun getActiveQuizzes(): Flow<List<Quiz>> {
        TODO("Not yet implemented")
    }

    override suspend fun setTheme(enableDarkTheme: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getTheme(): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getQuestionCandidateWords(): Flow<List<Word>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveQuizToRoom(quiz: Quiz) {
        TODO("Not yet implemented")
    }

    override suspend fun saveQuestionsToRoom(questions: List<Question>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateQuizIsCompletedInRoom(quizId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getWordByIdFromRoom(id: Int): Word {
        TODO("Not yet implemented")
    }

    override fun getQuizWithQuestionsById(quizId: Int): Flow<QuizWithQuestions> {
        TODO("Not yet implemented")
    }

    override suspend fun updateQuizzesToRoom(quizzes: List<Quiz>) {
        TODO("Not yet implemented")
    }

    override suspend fun setWordToFirestore(uid: String, word: Word) {
        TODO("Not yet implemented")
    }

    override suspend fun setQuizToFirestore(uid: String, quiz: Quiz) {
        TODO("Not yet implemented")
    }

    override suspend fun setQuestionToFirestore(uid: String, question: Question) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWordFromFirestore(uid: String, word: Word) {
        TODO("Not yet implemented")
    }

    override fun getCurrentUserUid(): String {
        TODO("Not yet implemented")
    }

    override suspend fun updateQuizToRoom(quiz: Quiz) {
        TODO("Not yet implemented")
    }

    override suspend fun updateWordInRoom(word: Word) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllWords() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllQuestion() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllQuiz() {
        TODO("Not yet implemented")
    }

    override suspend fun getWordsFromFirestore(uid: String): List<Word> {
        TODO("Not yet implemented")
    }

    override suspend fun getQuestionsFromFirestore(uid: String, quizId: String): List<Question> {
        TODO("Not yet implemented")
    }

    override suspend fun getQuizzesFromFirestore(uid: String): List<Quiz> {
        TODO("Not yet implemented")
    }

    override fun getQuizzesFromRoom(): Flow<List<Quiz>> {
        TODO("Not yet implemented")
    }

    override fun syncDataWorker() {
        TODO("Not yet implemented")
    }

    override suspend fun translateWord(word: String): Resource<Translation> {
        TODO("Not yet implemented")
    }

    override fun getWordsFromRoomByGroupId(groupId: Int?): Flow<List<Word>> {
        TODO("Not yet implemented")
    }

    override fun getGroupsFromRoom(): Flow<List<Group>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateGroupFromRoom(group: Group) {
        TODO("Not yet implemented")
    }

    override suspend fun insertGroupFromRoom(group: Group) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGroupFromRoom(group: Group) {
        TODO("Not yet implemented")
    }

    override suspend fun setGroupFromFirestore(group: Group, uid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGroupFromFirestore(group: Group, uid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getGroupsFromFirestore(uid: String): List<Group> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteQuestionFromFirestore(uid: String, question: Question) {
        TODO("Not yet implemented")
    }

    override fun getQuestionFromActiveQuizzesByWordId(wordId: Int): Flow<List<Question>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteQuestionFromRoom(question: Question) {
        TODO("Not yet implemented")
    }

    override suspend fun setMeaningVisibility(meaningVisibility: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getMeaningVisibility(): Flow<Boolean?> {
        TODO("Not yet implemented")
    }

    override suspend fun setActiveGroupId(groupId: Int) {
        TODO("Not yet implemented")
    }

    override fun getActiveGroupId(): Flow<Int?> {
        TODO("Not yet implemented")
    }

    override suspend fun sendResetEmail(email: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUserAccount() {
        TODO("Not yet implemented")
    }

    override suspend fun reAuthenticateUser(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllGroups() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllWordsInFirestore(uid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllQuizzesInFirestore(uid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllQuestionsInFirestore(uid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllGroupsInFirestore(uid: String) {
        TODO("Not yet implemented")
    }
}