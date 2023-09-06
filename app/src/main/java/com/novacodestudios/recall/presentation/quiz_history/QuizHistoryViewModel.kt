package com.novacodestudios.recall.presentation.quiz_history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacodestudios.recall.domain.use_case.GetActiveQuizzes
import com.novacodestudios.recall.domain.use_case.GetCompletedQuizzes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class QuizHistoryViewModel @Inject constructor(
    getCompletedQuizzes: GetCompletedQuizzes,
    getActiveQuizzes: GetActiveQuizzes
) : ViewModel() {
    var state by mutableStateOf(QuizHistoryState())

    private var completedQuizzesJob: Job? = null
    private var activeQuizzesJob: Job? = null

    init {
        completedQuizzesJob?.cancel()
        activeQuizzesJob?.cancel()

        completedQuizzesJob = getCompletedQuizzes().onEach { completedQuizzes ->
            state=state.copy(pastQuizzes = completedQuizzes)
        }.launchIn(viewModelScope)

        activeQuizzesJob= getActiveQuizzes().onEach {activeQuizList->
            if (activeQuizList.isNotEmpty()){
                state=state.copy(activeQuiz = activeQuizList.first())
            }

        }.launchIn(viewModelScope)



    }
}