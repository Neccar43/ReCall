package com.novacodestudios.recall.presentation.result

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacodestudios.recall.domain.use_case.GetQuizWithQuestionsById
import com.novacodestudios.recall.util.Constants.QUIZ_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
@HiltViewModel
class ResultViewModel@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getQuizWithQuestionsById: GetQuizWithQuestionsById
): ViewModel() {
    var state by mutableStateOf(ResultState())
    init {
        savedStateHandle.get<Int>(QUIZ_ID)?.let {quizId->
            getQuizWithQuestionsById(quizId).onEach {quizWithQuestions ->
                state=state.copy(
                    quiz = quizWithQuestions.quiz,
                    questions = quizWithQuestions.questions
                )
            }.launchIn(viewModelScope)
        }
    }

}