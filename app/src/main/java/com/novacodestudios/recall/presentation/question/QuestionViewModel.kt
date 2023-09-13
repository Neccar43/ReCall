package com.novacodestudios.recall.presentation.question

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacodestudios.recall.domain.use_case.CalculateNextRepetitionDate
import com.novacodestudios.recall.domain.use_case.GetQuizWithQuestionsById
import com.novacodestudios.recall.domain.use_case.GetWordByIdFromRoom
import com.novacodestudios.recall.domain.use_case.SetQuestionToFirestore
import com.novacodestudios.recall.domain.use_case.SetQuizToFirestore
import com.novacodestudios.recall.domain.use_case.SetWordToFirestore
import com.novacodestudios.recall.domain.use_case.UpdateQuestionInRoom
import com.novacodestudios.recall.domain.use_case.UpdateQuizInRoom
import com.novacodestudios.recall.domain.use_case.UpdateWordsInRoom
import com.novacodestudios.recall.util.Constants.QUIZ_ID
import com.novacodestudios.recall.util.updateElementByIndex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val calculateNextRepetitionDate: CalculateNextRepetitionDate,
    private val getWordByIdFromRoom: GetWordByIdFromRoom,
    private val updateWordsInRoom: UpdateWordsInRoom,
    private val setWordToFirestore: SetWordToFirestore,
    private val updateQuestionInRoom: UpdateQuestionInRoom,
    private val setQuestionToFirestore: SetQuestionToFirestore,
    private val getQuizWithQuestionsById: GetQuizWithQuestionsById,
    private val updateQuizInRoom: UpdateQuizInRoom,
    private val setQuizToFirestore: SetQuizToFirestore,
) : ViewModel() {

    var state by mutableStateOf(QuestionState())

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow= _eventFlow.asSharedFlow()


    private var job: Job? = null

    init {
        job?.cancel()
        savedStateHandle.get<Int>(QUIZ_ID)?.let { quizId ->
            job = getQuizWithQuestionsById(quizId).onEach { quizWithQuestions ->
                state = state.copy(
                    questions = quizWithQuestions.questions,
                    quiz = quizWithQuestions.quiz
                )
            }.launchIn(viewModelScope)
        }
    }

    fun onEvent(event: QuestionEvent) {
        when (event) {
            is QuestionEvent.CancelQuiz -> TODO()
            is QuestionEvent.AnswerChange -> state = state.copy(answer = event.answer)
            is QuestionEvent.SubmitAnswer -> answerQuestion(event.answer, event.responseTime)
            is QuestionEvent.StartTime -> state = state.copy(startTime = System.currentTimeMillis())
            is QuestionEvent.FinishQuiz -> finishQuiz()
        }
    }

    private fun finishQuiz() {
        viewModelScope.launch {
            state=state.copy(isLoading = true)

            state.questions.forEach {
                val question=it.copy(version = it.version+1)
                updateQuestionInRoom(question)
                setQuestionToFirestore(question)
            }

            state.quiz?.let {
                val quiz=it.copy(isCompleted = true, version = it.version+1)
                updateQuizInRoom(quiz)
                setQuizToFirestore(quiz)
            }

            val calculatedWords = state.questions.map { question ->
                val word = getWordByIdFromRoom(question.wordId!!)

                calculateNextRepetitionDate(
                    word = word.copy(isInQuiz = false, version = word.version+1),
                    isAnswerCorrect = question.correctAnswer == question.userAnswer,
                    responseTime = question.responseTime!!
                )
            }

            updateWordsInRoom(calculatedWords)
            calculatedWords.forEach { setWordToFirestore(it) }
            state=state.copy(isLoading = false)
            _eventFlow.emit(UIEvent.FinishQuiz)

        }
    }

    private fun answerQuestion(answer: String, responseTime: Long) {
        val question = state.questions[state.questionIndex]

        state = state.copy(
            questions = state.questions
                .updateElementByIndex(
                    state.questionIndex,
                    question.copy(
                        userAnswer = answer.lowercase(Locale.ROOT),
                        responseTime = responseTime
                    )
                ),
            answer = ""
        )
        if (state.questions.size != state.questionIndex + 1) {
            state = state.copy(questionIndex = state.questionIndex + 1)
        }
    }
    sealed class UIEvent{
        data object FinishQuiz:UIEvent()
    }
}