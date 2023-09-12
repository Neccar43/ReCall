package com.novacodestudios.recall.domain.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.domain.model.Synchronizable
import com.novacodestudios.recall.domain.repository.ReCallRepository
import com.novacodestudios.recall.util.Constants.TAG
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

@HiltWorker
class SyncDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ReCallRepository,
) :
    CoroutineWorker(appContext, workerParams) {
    companion object {
        const val WORK_NAME = "sync_worker"
        const val IS_PRIMARY_DB_ROOM = "isPrimaryDBRoom"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "SyncDataWorker:doWork başladı")
        return try {
            coroutineScope {
                repository.apply {
                    val isPrimaryDBRoom = inputData.getBoolean(IS_PRIMARY_DB_ROOM, false)
                    val uid = getCurrentUserUid()

                    val wordsFromFirestore = async { getWordsFromFirestore(uid) }
                    val quizzesFromFirestore = async { getQuizzesFromFirestore(uid) }
                    val questionsFromFirestore = mutableListOf<List<Question>>()

                    quizzesFromFirestore.await().forEach { quiz ->
                        val quizId = quiz.id.toString()
                        val questionList = async { getQuestionsFromFirestore(uid, quizId) }
                        questionsFromFirestore.add(questionList.await())
                    }
                    Log.d(TAG, "SyncDataWorker:firestore'dan veriler alındı")

                    val wordsFromRoom = async { getWordsFromRoom().first() }
                    val quizzesFromRoom = async { getQuizzesFromRoom().first() }
                    val questionsFromRoom = mutableListOf<List<Question>>()

                    quizzesFromRoom.await().forEach { quiz ->
                        val quizId = quiz.id
                        val questionList = async { getQuestionsByQuizId(quizId).first() }
                        questionsFromRoom.add(questionList.await())
                    }
                    Log.d(TAG, "SyncDataWorker:Room'dan veriler alındı")

                    if (isPrimaryDBRoom) {
                        Log.d(TAG, "SyncDataWorker:primary database Room")
                        findElementsToAdd(
                            wordsFromRoom.await(),
                            wordsFromFirestore.await()
                        ).forEach { setWordToFirestore(uid, it) }

                        findElementsToAdd(
                            quizzesFromRoom.await(),
                            quizzesFromFirestore.await()
                        ).forEach { setQuizToFirestore(uid, it) }

                        findElementsToAdd(
                            questionsFromRoom.flatten(),
                            questionsFromFirestore.flatten()
                        ).forEach { setQuestionToFirestore(uid, it) }

                        findElementsToUpdate(
                            wordsFromRoom.await(),
                            wordsFromFirestore.await()
                        ).forEach { setWordToFirestore(uid, it) }

                        findElementsToUpdate(
                            quizzesFromRoom.await(),
                            quizzesFromFirestore.await()
                        ).forEach { setQuizToFirestore(uid, it) }

                        findElementsToUpdate(
                            questionsFromRoom.flatten(),
                            questionsFromFirestore.flatten()
                        ).forEach { setQuestionToFirestore(uid, it) }

                        findElementsToDelete(
                            wordsFromRoom.await(),
                            wordsFromFirestore.await()
                        ).forEach { deleteWordFromFirestore(uid, it) }

                    } else {
                        Log.d(TAG, "SyncDataWorker:primary database Firestore")
                        findElementsToAdd(
                            wordsFromFirestore.await(),
                            wordsFromRoom.await()
                        ).forEach { saveWordToRoom(it) }

                        findElementsToAdd(
                            quizzesFromFirestore.await(),
                            quizzesFromRoom.await()
                        ).forEach { saveQuizToRoom(it) }

                        findElementsToAdd(
                            questionsFromFirestore.flatten(),
                            questionsFromRoom.flatten()
                        ).also { saveQuestionsToRoom(it) }

                        findElementsToUpdate(
                            wordsFromFirestore.await(),
                            wordsFromRoom.await()
                        ).also { updateWordsToRoom(it) }

                        findElementsToUpdate(
                            quizzesFromFirestore.await(),
                            quizzesFromRoom.await()
                        ).also { updateQuizzesToRoom(it) }

                        findElementsToUpdate(
                            questionsFromFirestore.flatten(),
                            questionsFromRoom.flatten()
                        ).forEach { updateQuestionToRoom(it) }

                        findElementsToDelete(
                            wordsFromFirestore.await(),
                            wordsFromRoom.await()
                        ).forEach {deleteWordFromRoom(it) }
                    }
                }
            }
            Log.d(TAG, "SyncDataWorker:doWork bitti")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "SyncDataWorker: $e")
            Result.failure()
        }
    }


    private fun <T : Synchronizable> findElementsToAdd(
        primaryElements: List<T>,
        targetElements: List<T>,
    ): List<T> =
        primaryElements.filter { primaryElement -> !targetElements.any { it.id == primaryElement.id } }
            .also { Log.d(TAG, "findElementsToAdd: $it") }


    private fun <T : Synchronizable> findElementsToUpdate(
        primaryElements: List<T>,
        targetElements: List<T>,
    ): List<T> =
        primaryElements.filter { primaryElement -> targetElements.any { primaryElement.id == it.id && primaryElement.version > it.version } }
            .also { Log.d(TAG, "findElementsToUpdate: $it") }

    private fun <T : Synchronizable> findElementsToDelete(
        primaryElements: List<T>,
        targetElements: List<T>,
    ): List<T> =
        targetElements.filter { targetElement -> !primaryElements.any { targetElement.id == it.id } }
            .also { Log.d(TAG, "findElementsToDelete: $it") }
}
