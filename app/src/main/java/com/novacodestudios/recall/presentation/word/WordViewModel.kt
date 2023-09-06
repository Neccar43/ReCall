package com.novacodestudios.recall.presentation.word

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.use_case.DeleteWordFromFirestore
import com.novacodestudios.recall.domain.use_case.DeleteWordFromRoom
import com.novacodestudios.recall.domain.use_case.GetWordsBySearch
import com.novacodestudios.recall.domain.use_case.GetWordsFromRoom
import com.novacodestudios.recall.domain.use_case.SaveWordToRoom
import com.novacodestudios.recall.domain.use_case.SetWordToFirestore
import com.novacodestudios.recall.domain.use_case.UpdateWordInRoom
import com.novacodestudios.recall.domain.use_case.ValidateMeaning
import com.novacodestudios.recall.domain.use_case.ValidateWord
import com.novacodestudios.recall.util.Resource
import com.novacodestudios.recall.util.isNotNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(
    private val saveWordToRoom: SaveWordToRoom,
    private val updateWordInRoom: UpdateWordInRoom,
    private val setWordToFirestore: SetWordToFirestore,
    private val validateWord: ValidateWord,
    private val validateMeaning: ValidateMeaning,
    private val getWordsFromRoom: GetWordsFromRoom,
    private val getWordsBySearch: GetWordsBySearch,
    private val deleteWordFromRoom: DeleteWordFromRoom,
    private val deleteWordFromFirestore: DeleteWordFromFirestore,
) : ViewModel() {

    var state by mutableStateOf(WordState())

    fun onEvent(event: WordEvent) {
        when (event) {
            is WordEvent.OnMeaningChanged -> state = state.copy(meaning = event.meaning)
            is WordEvent.SaveWord -> saveWord()
            is WordEvent.OnWordChanged -> state = state.copy(word = event.word)
            is WordEvent.OnSearchChanged -> searchWorld(event.search)
            is WordEvent.OnAddDialogVisibilityChange -> {
                state = state.copy(
                    isAddDialogVisible = event.isVisible,
                    wordError = null,
                    meaningError = null
                )
            }

            is WordEvent.DeleteWord -> deleteWord()
            is WordEvent.UpdateWord -> updateWord()
            is WordEvent.OnDeleteDialogVisibilityChange -> state = state.copy(
                isDeleteDialogVisible = event.isVisible,
                deletedWord = event.deletedWord
            )

            is WordEvent.OnUpdateDialogVisibilityChange -> state =
                state.copy(
                    isUpdateDialogVisible = event.isVisible,
                    wordError = null,
                    meaningError = null
                )

            is WordEvent.OnSetUpdatedWord -> state = state.copy(
                updatedWord = event.updatedWord,
                word = event.updatedWord.name,
                meaning = event.updatedWord.meaning
            )
        }
    }

    private fun updateWord() {

        val wordResult = validateWord(state.word)
        val meaningResult = validateMeaning(state.meaning)
        val redundantUpdateResult =
            isRedundantUpdate(state.word, state.meaning, state.updatedWord!!)
        val hasError =
            listOf(wordResult, meaningResult, redundantUpdateResult).any { it.data != true }

        if (hasError) {
            if (redundantUpdateResult.message.isNotNull()) {
                state = state.copy(
                    wordError = redundantUpdateResult.message,
                    meaningError = redundantUpdateResult.message
                )
                return
            }
            state = state.copy(
                wordError = wordResult.message,
                meaningError = meaningResult.message
            )
            return
        }
        viewModelScope.launch {
            val updatedWord = state.updatedWord!!.copy(
                name = state.word,
                meaning = state.meaning,
                version = state.updatedWord!!.version + 1,
            )
            updateWordInRoom(updatedWord)
            setWordToFirestore(updatedWord)
            state=state.copy(isUpdateDialogVisible = false)
        }

    }

    private fun isRedundantUpdate(
        newName: String,
        newMeaning: String,
        oldWord: Word
    ): Resource<Boolean> {
        if (oldWord.name == newName && oldWord.meaning == newMeaning)
            return Resource.Error(message = "Yeni kelime önciki ile aynı olamaz")

        return Resource.Success(true)
    }

    private fun deleteWord() {
        viewModelScope.launch {
            deleteWordFromRoom(state.deletedWord!!)
            deleteWordFromFirestore(state.deletedWord!!)
            state=state.copy(isDeleteDialogVisible = false)
        }
    }

    private var searchJob: Job? = null

    private fun searchWorld(search: String) {
        state = state.copy(search = search)
        searchJob?.cancel()
        searchJob = getWordsBySearch(state.search).onEach { words ->
            state = state.copy(wordList = words)
            delay(500L)
        }.launchIn(viewModelScope)


    }

    init {
        getWords()
    }

    private var job: Job? = null

    private fun getWords() {
        job?.cancel()
        job = getWordsFromRoom().onEach { words ->
            state = state.copy(wordList = words)
        }.launchIn(viewModelScope)

    }

    private fun saveWord() {
        val wordResult = validateWord(state.word)
        val meaningResult = validateMeaning(state.meaning)
        val hasError = listOf(wordResult, meaningResult).any { it.data != true }

        if (hasError) {
            state = state.copy(
                wordError = wordResult.message,
                meaningError = meaningResult.message
            )
            return
        }
        viewModelScope.launch {
            val word = Word(
                name = state.word.lowercase(Locale.ROOT),
                meaning = state.meaning.lowercase(Locale.ROOT)
            )
            saveWordToRoom(word)
            setWordToFirestore(word)
            state = state.copy(
                word = "",
                meaning = "",
                wordError = null,
                meaningError = null,
                isAddDialogVisible = false
            )
        }
    }

}