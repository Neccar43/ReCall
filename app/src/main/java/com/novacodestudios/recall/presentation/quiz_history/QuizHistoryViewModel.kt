package com.novacodestudios.recall.presentation.quiz_history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacodestudios.recall.domain.model.QuizDetail
import com.novacodestudios.recall.domain.use_case.GetActiveQuizzes
import com.novacodestudios.recall.domain.use_case.GetCompletedQuizzes
import com.novacodestudios.recall.domain.use_case.GetQuizWithQuestionsById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class QuizHistoryViewModel @Inject constructor(
    getCompletedQuizzes: GetCompletedQuizzes,
    getActiveQuizzes: GetActiveQuizzes,
    private val getQuizWithQuestionsById: GetQuizWithQuestionsById
) : ViewModel() {
    var state by mutableStateOf(QuizHistoryState())

    private var completedQuizzesJob: Job? = null
    private var activeQuizzesJob: Job? = null

    init {
        completedQuizzesJob?.cancel()
        activeQuizzesJob?.cancel()

        val quizDetails= mutableListOf<QuizDetail>()

        completedQuizzesJob = getCompletedQuizzes().onEach { completedQuizzes ->
            completedQuizzes.forEach {quiz->
                getQuizWithQuestionsById(quiz.id).map {it?.toQuizDetail() }.onEach {
                    if (it != null) {
                        quizDetails.add(it)
                    }
                    state=state.copy(pastQuizzes = quizDetails)
                }.launchIn(viewModelScope)
            }

        }.launchIn(viewModelScope)



        activeQuizzesJob= getActiveQuizzes().onEach {activeQuizList->
            if (activeQuizList.isNotEmpty()){
                state=state.copy(activeQuiz = activeQuizList.first())
            }

        }.launchIn(viewModelScope)



    }
}