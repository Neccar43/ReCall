package com.novacodestudios.recall.data.repository

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.novacodestudios.recall.data.datastore.GroupDataStore
import com.novacodestudios.recall.data.datastore.MeaningVisibilityDataStore
import com.novacodestudios.recall.data.datastore.ThemeDatastore
import com.novacodestudios.recall.data.local.ReCallDao
import com.novacodestudios.recall.data.mapper.toTranslation
import com.novacodestudios.recall.data.remote.GoogleAuthUiClient
import com.novacodestudios.recall.data.remote.TranslationApi
import com.novacodestudios.recall.data.util.FirestoreCollections
import com.novacodestudios.recall.domain.algorithm.SpacedRepetitionAlgorithm
import com.novacodestudios.recall.domain.model.Group
import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.model.Quiz
import com.novacodestudios.recall.domain.model.Translation
import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.model.relation.QuizWithQuestions
import com.novacodestudios.recall.domain.model.toGroup
import com.novacodestudios.recall.domain.model.toQuestion
import com.novacodestudios.recall.domain.model.toQuiz
import com.novacodestudios.recall.domain.model.toWord
import com.novacodestudios.recall.domain.repository.ReCallRepository
import com.novacodestudios.recall.domain.worker.SyncDataWorker
import com.novacodestudios.recall.presentation.util.UIText
import com.novacodestudios.recall.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReCallRepositoryImpl @Inject constructor(
    private val dao: ReCallDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val algorithm: SpacedRepetitionAlgorithm,
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val datastore: ThemeDatastore,
    private val workManager: WorkManager,
    private val api: TranslationApi,
    private val meaningVisibilityDataStore: MeaningVisibilityDataStore,
    private val groupDataStore: GroupDataStore,

    ) : ReCallRepository {
    override fun getWordsFromRoom(): Flow<List<Word>> {
        return dao.getAllWordsFromRoom()
    }

    override suspend fun saveWordToRoom(word: Word) {
        dao.insertWordToRoom(word)
    }

    override suspend fun deleteWordFromRoom(word: Word) {
        dao.deleteWordFromRoom(word)
    }

    override suspend fun updateWordsToRoom(words: List<Word>) {
        dao.updateWordsInRoom(*words.toTypedArray())
    }

    override fun getQuestionsByQuizId(quizId: Int): Flow<List<Question>> {
        return dao.getQuestionsByQuizId(quizId)
    }

    override suspend fun updateQuestionToRoom(question: Question) {
        dao.updateQuestionInRoom(question)
    }

    override fun getUpcomingQuizzesWithQuestions(): Flow<List<QuizWithQuestions>> {
        return dao.getUpcomingQuizzesWithQuestions()
    }

    override suspend fun shouldQuizCreate(): Boolean {
        return dao.shouldCreateQuiz()
    }

    override fun calculateNextRepetitionDate(
        word: Word,
        isAnswerCorrect: Boolean,
        responseTime: Long,
    ): Word {
        return algorithm.calculateNextRepetitionDate(word, isAnswerCorrect, responseTime)
    }

    override suspend fun signUpUserToFirebase(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()

    }

    override suspend fun signInUserWithFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun signInUserWithGoogle() {
        googleAuthUiClient.signIn()
    }

    override suspend fun signOutUser() {
        googleAuthUiClient.signOut()
    }

    override suspend fun saveUserToFirestore(fullName: String, email: String) {
        firestore
    }

    override fun searchWords(search: String, groupId: Int?): Flow<List<Word>> {
        return dao.searchWords(search, groupId)
    }

    override fun getCompletedQuizzes(): Flow<List<Quiz>> {
        return dao.getCompletedQuizzesFromRoom()
    }

    override fun getActiveQuizzes(): Flow<List<Quiz>> {
        return dao.getActiveQuizzesFromRoom()
    }

    override suspend fun setTheme(enableDarkTheme: Boolean) {
        datastore.setTheme(enableDarkTheme)
    }

    override fun getTheme(): Flow<Boolean> {
        return datastore.getTheme()
    }

    override fun getQuestionCandidateWords(): Flow<List<Word>> {
        return dao.getQuestionCandidateWords()
    }

    override suspend fun saveQuizToRoom(quiz: Quiz) {
        dao.insertQuizToRoom(quiz)
    }

    override suspend fun saveQuestionsToRoom(questions: List<Question>) {
        return dao.insertQuestions(*questions.toTypedArray())
    }

    override suspend fun updateQuizIsCompletedInRoom(quizId: Int) {
        dao.updateQuizIsCompleted(quizId)
    }

    override suspend fun getWordByIdFromRoom(id: Int): Word {
        return dao.getWordById(id)
    }

    override fun getQuizWithQuestionsById(quizId: Int): Flow<QuizWithQuestions> {
        return dao.getQuizWithQuestionsByQuizId(quizId)
    }


    override suspend fun updateQuizzesToRoom(quizzes: List<Quiz>) {
        dao.updateQuizzes(*quizzes.toTypedArray())
    }

    override suspend fun setWordToFirestore(uid: String, word: Word) {
        try {
            firestore
                .collection(FirestoreCollections.WORDS)
                .document(uid)
                .collection(FirestoreCollections.WORDS)
                .document(word.id.toString())
                .set(word.toMap()).await()
        } catch (e: Exception) {
            throw e
        }


    }

    override suspend fun setQuizToFirestore(uid: String, quiz: Quiz) {
        try {
            firestore
                .collection(FirestoreCollections.QUIZZES)
                .document(uid)
                .collection(FirestoreCollections.QUIZZES)
                .document(quiz.id.toString())
                .set(quiz.toMap()).await()
        } catch (e: Exception) {
            throw e
        }

    }

    override suspend fun setQuestionToFirestore(uid: String, question: Question) {
        try {
            firestoreQuestionRef(uid, question)
                .set(question.toMap()).await()
        } catch (e: Exception) {
            throw e
        }

    }


    override suspend fun deleteWordFromFirestore(uid: String, word: Word) {
        try {
            firestore
                .collection(FirestoreCollections.WORDS)
                .document(uid)
                .collection(FirestoreCollections.WORDS)
                .document(word.id.toString())
                .delete()
                .await()
        } catch (e: Exception) {
            throw e
        }

    }

    override fun getCurrentUserUid(): String {
        return auth.currentUser?.uid ?: ""
    }

    override suspend fun updateQuizToRoom(quiz: Quiz) {
        dao.updateQuiz(quiz)
    }

    override suspend fun updateWordInRoom(word: Word) {
        dao.updateWord(word)
    }

    override suspend fun deleteAllWords() {
        dao.deleteAllWords()
    }

    override suspend fun deleteAllQuestion() {
        dao.deleteAllQuestion()
    }

    override suspend fun deleteAllQuiz() {
        dao.deleteAllQuizzes()
    }

    override suspend fun getWordsFromFirestore(uid: String): List<Word> {
        try {
            val querySnapshot = firestore
                .collection(FirestoreCollections.WORDS)
                .document(uid)
                .collection(FirestoreCollections.WORDS)
                .get().await()

            val wordList = mutableListOf<Word>()

            querySnapshot.forEach {
                val word = it.toWord()
                wordList.add(word)
            }
            return wordList.toList()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getQuestionsFromFirestore(uid: String, quizId: String): List<Question> {
        try {
            val querySnapshot = firestore
                .collection(FirestoreCollections.QUESTIONS)
                .document(uid)
                .collection(FirestoreCollections.QUESTIONS)
                .document(quizId)
                .collection(FirestoreCollections.QUESTIONS)
                .get().await()
            val questionList = mutableListOf<Question>()

            querySnapshot.forEach {
                val question = it.toQuestion()
                questionList.add(question)
            }
            return questionList.toList()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getQuizzesFromFirestore(uid: String): List<Quiz> {
        try {
            val querySnapshot = firestore
                .collection(FirestoreCollections.QUIZZES)
                .document(uid)
                .collection(FirestoreCollections.QUIZZES)
                .get().await()

            val quizList = mutableListOf<Quiz>()

            querySnapshot.forEach {
                val quiz = it.toQuiz()
                quizList.add(quiz)
            }
            return quizList.toList()
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getQuizzesFromRoom(): Flow<List<Quiz>> {
        return dao.getAllQuizzesFromRoom()
    }

    override fun syncDataWorker() {
        val syncConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val oneTimeSyncDataRequest = OneTimeWorkRequestBuilder<SyncDataWorker>()
            .setInputData(workDataOf(SyncDataWorker.IS_PRIMARY_DB_ROOM to false))
            .setConstraints(syncConstraints)
            .addTag(SyncDataWorker.WORK_NAME)
            .build()

        workManager.enqueue(oneTimeSyncDataRequest)
    }

    override suspend fun translateWord(word: String): Resource<Translation> {
        try {
            val response = api.translateText(word)
            if (response.isSuccessful) {
                val translationDto =
                    response.body()
                        ?: return Resource.Error(message = UIText.DynamicText("TranslationDto is null."))
                return Resource.Success(translationDto.toTranslation(word))
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error."
                return Resource.Error(message = UIText.DynamicText(errorMessage))
            }
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Unknown error."
            return Resource.Error(message = UIText.DynamicText(errorMessage))
        }

    }

    override fun getWordsFromRoomByGroupId(groupId: Int?): Flow<List<Word>> {
        return if (groupId == null) dao.getAllWordsFromRoom() else dao.getWordsByGroupId(groupId)
    }

    override fun getGroupsFromRoom(): Flow<List<Group>> {
        return dao.getGroups()
    }

    override suspend fun updateGroupFromRoom(group: Group) {
        dao.updateGroup(group)
    }

    override suspend fun insertGroupFromRoom(group: Group) {
        dao.insertGroup(group)
    }

    override suspend fun deleteGroupFromRoom(group: Group) {
        dao.deleteGroup(group)
    }

    override suspend fun setGroupFromFirestore(group: Group, uid: String) {
        try {
            firestoreGroupRef(group.id.toString(), uid)
                .set(group.toMap()).await()
        } catch (e: Exception) {
            throw e
        }

    }

    override suspend fun deleteGroupFromFirestore(group: Group, uid: String) {
        try {
            firestoreGroupRef(group.id.toString(), uid)
                .delete().await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getGroupsFromFirestore(uid: String): List<Group> {
        try {
            val querySnapshot = firestore.collection(FirestoreCollections.GROUPS)
                .document(uid)
                .collection(FirestoreCollections.GROUPS)
                .get().await()

            val groups = mutableListOf<Group>()

            querySnapshot.forEach {
                groups.add(it.toGroup())
            }
            return groups
        } catch (e: Exception) {
            throw e
        }
    }


    override suspend fun deleteQuestionFromFirestore(uid: String, question: Question) {
        try {
            firestoreQuestionRef(uid, question).delete().await()
        } catch (e: Exception) {
            throw e
        }

    }

    override fun getQuestionFromActiveQuizzesByWordId(wordId: Int): Flow<List<Question>> {
        return dao.getQuestionFromActiveQuizzesByWordId(wordId)
    }

    override suspend fun deleteQuestionFromRoom(question: Question) {
        dao.deleteQuestion(question)
    }


    private fun firestoreGroupRef(groupId: String, uid: String): DocumentReference {
        return firestore.collection(FirestoreCollections.GROUPS)
            .document(uid)
            .collection(FirestoreCollections.GROUPS)
            .document(groupId)
    }

    private fun firestoreQuestionRef(uid: String, question: Question): DocumentReference {
        return firestore
            .collection(FirestoreCollections.QUESTIONS)
            .document(uid)
            .collection(FirestoreCollections.QUESTIONS)
            .document(question.quizId.toString())
            .collection(FirestoreCollections.QUESTIONS)
            .document(question.id.toString())
    }

    override fun getMeaningVisibility(): Flow<Boolean?> {
        return meaningVisibilityDataStore.getMeaningVisibility()
    }

    override suspend fun setActiveGroupId(groupId: Int) {
        groupDataStore.setGroupId(groupId)
    }

    override fun getActiveGroupId(): Flow<Int?> {
        return groupDataStore.getGroupId()
    }

    override suspend fun setMeaningVisibility(meaningVisibility: Boolean) {
        meaningVisibilityDataStore.setMeaningVisibility(meaningVisibility)
    }

    override suspend fun sendResetEmail(email: String) {
        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: Exception) {
            throw e
        }

    }

    override suspend fun deleteUserAccount() {
        auth.currentUser!!.delete().await()
    }

    override suspend fun reAuthenticateUser(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        auth.currentUser!!.reauthenticate(credential).await()
    }

    override suspend fun deleteAllGroups() {
        dao.deleteAllGroups()
    }

    override suspend fun deleteAllWordsInFirestore(uid: String) {
        try {
            firestore
                .collection(FirestoreCollections.WORDS)
                .document(uid).delete().await()
        } catch (e: Exception) {
            throw e
        }

    }

    override suspend fun deleteAllQuizzesInFirestore(uid: String) {
        try {
            firestore
                .collection(FirestoreCollections.QUIZZES)
                .document(uid).delete().await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllQuestionsInFirestore(uid: String) {
        try {
            firestore
                .collection(FirestoreCollections.QUESTIONS)
                .document(uid).delete().await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllGroupsInFirestore(uid: String) {
        try {
            firestore
                .collection(FirestoreCollections.GROUPS)
                .document(uid).delete().await()
        } catch (e: Exception) {
            throw e
        }
    }
}