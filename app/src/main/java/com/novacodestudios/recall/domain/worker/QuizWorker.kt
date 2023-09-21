package com.novacodestudios.recall.domain.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.novacodestudios.recall.R
import com.novacodestudios.recall.domain.model.copyAll
import com.novacodestudios.recall.domain.model.toQuizWithQuestions
import com.novacodestudios.recall.domain.repository.ReCallRepository
import com.novacodestudios.recall.presentation.main_activity.MainActivity
import com.novacodestudios.recall.util.Constants.TAG
import com.novacodestudios.recall.util.NotiKitBuilder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltWorker
class QuizWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ReCallRepository,
) :
    CoroutineWorker(appContext, workerParams) {
    companion object {
        private const val MAX_QUESTION_SIZE = 10
        const val WORK_NAME = "quiz_worker"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "QuizWorker:doWork başladı")
        try {
            repository.apply {
                if (shouldQuizCreate()) {
                    coroutineScope {
                        val quizJob=launch {
                            Log.d(TAG, "QuizWorker: doWork if bloğu çalıştı")
                            val uid = getCurrentUserUid()

                            val quizWords = async {
                                getQuestionCandidateWords().map { words ->
                                    words.toMutableList().shuffle()
                                    words.chunked(MAX_QUESTION_SIZE)
                                        .dropLastWhile { it.size < MAX_QUESTION_SIZE }
                                }.first()
                            }
                            quizWords.await().forEach { words ->
                                val quizWithQuestions = words.toQuizWithQuestions()
                                val updatedWords=words.copyAll(isInQuiz = true)
                                launch { saveQuizToRoom(quizWithQuestions.quiz)}
                                launch { saveQuestionsToRoom(quizWithQuestions.questions)}
                                launch { updateWordsToRoom(updatedWords)}
                                launch { setQuizToFirestore(uid,quizWithQuestions.quiz) }
                                launch {quizWithQuestions.questions.forEach {setQuestionToFirestore(uid,it) }}
                                launch { updatedWords.forEach { setWordToFirestore(uid,it) } }
                            }
                        }
                        quizJob.join()
                    }

                    NotiKitBuilder(applicationContext)
                        .setChannel("quiz_id", "Quiz", "For quizzes")
                        .setContent("Hazır bir quiz mevcut", "Quizini hemen olmak için tıkla")
                        .setIntent(Intent(applicationContext, MainActivity::class.java))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .build()
                }
            }

            Log.d(TAG, "QuizWorker: doWork bitti")
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

}