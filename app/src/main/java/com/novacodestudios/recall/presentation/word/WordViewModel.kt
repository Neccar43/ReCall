package com.novacodestudios.recall.presentation.word

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacodestudios.recall.domain.model.Group
import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.use_case.DeleteGroupFromFirestore
import com.novacodestudios.recall.domain.use_case.DeleteGroupFromRoom
import com.novacodestudios.recall.domain.use_case.DeleteWordFromFirestore
import com.novacodestudios.recall.domain.use_case.DeleteWordFromRoom
import com.novacodestudios.recall.domain.use_case.GetGroupsFromRoom
import com.novacodestudios.recall.domain.use_case.GetWordsBySearch
import com.novacodestudios.recall.domain.use_case.GetWordsFromRoomByGroupId
import com.novacodestudios.recall.domain.use_case.SaveGroupToRoom
import com.novacodestudios.recall.domain.use_case.SaveWordToRoom
import com.novacodestudios.recall.domain.use_case.SetGroupToFirestore
import com.novacodestudios.recall.domain.use_case.SetWordToFirestore
import com.novacodestudios.recall.domain.use_case.TranslateWord
import com.novacodestudios.recall.domain.use_case.UpdateGroupFromRoom
import com.novacodestudios.recall.domain.use_case.UpdateWordInRoom
import com.novacodestudios.recall.domain.use_case.ValidateMeaning
import com.novacodestudios.recall.domain.use_case.ValidateWord
import com.novacodestudios.recall.domain.util.WordOrder
import com.novacodestudios.recall.util.Resource
import com.novacodestudios.recall.util.isNotNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
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
    private val getWordsFromRoomByGroupId: GetWordsFromRoomByGroupId,
    private val getWordsBySearch: GetWordsBySearch,
    private val deleteWordFromRoom: DeleteWordFromRoom,
    private val deleteWordFromFirestore: DeleteWordFromFirestore,
    private val translateWord: TranslateWord,
    private val getGroupsFromRoom: GetGroupsFromRoom,
    private val saveGroupToRoom: SaveGroupToRoom,
    private val setGroupToFirestore: SetGroupToFirestore,
    private val deleteGroupFromRoom: DeleteGroupFromRoom,
    private val deleteGroupFromFirestore: DeleteGroupFromFirestore,
    private val updateGroupFromRoom: UpdateGroupFromRoom,
) : ViewModel() {

    var state by mutableStateOf(WordState())

    private val _eventFlow = MutableSharedFlow<SnackBarEvent>()
    val eventFlow: SharedFlow<SnackBarEvent> = _eventFlow.asSharedFlow()

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

            is WordEvent.OnOrderChanged -> {
                changeIsOpen()
                if (state.wordOrder::class == event.wordOrder::class &&
                    state.wordOrder.orderType == event.wordOrder.orderType
                ) {
                    return
                }
                getWords(event.wordOrder)
                state = state.copy(wordOrder = event.wordOrder)
            }

            is WordEvent.OnSelectedGroupChanged -> {
                if (event.group == state.selectedGroup)
                    return
                state = state.copy(selectedGroup = event.group)
                getWords(state.wordOrder)
            }

            WordEvent.OnSheetVisibilityChange -> changeIsOpen()
            is WordEvent.OnWordSelected -> {
                val selectedWord = event.word
                val selectedWords = state.selectedWords.toMutableList()
                selectedWords.add(selectedWord)
                state = state.copy(selectedWords = selectedWords, isLongClicked = true)
            }

            is WordEvent.OnWordUnSelected -> {
                val unSelectedWord = event.word
                val selectedWords = state.selectedWords.toMutableList()
                selectedWords.remove(unSelectedWord)
                state = state.copy(selectedWords = selectedWords)
            }

            WordEvent.OnAllWordsSelected -> {
                state = state.copy(selectedWords = emptyList())
                state = state.copy(selectedWords = state.wordList)
            }

            WordEvent.OnAllWordsUnSelected -> state = state.copy(selectedWords = emptyList())
            WordEvent.OnLongClickedChange -> {
                state = state.copy(isLongClicked = false, selectedWords = emptyList())

            }

            WordEvent.OnGroupDialogVisibilityChanged -> state =
                state.copy(isGroupDialogVisible = !state.isGroupDialogVisible)

            WordEvent.OnGroupAdded -> saveGroup()
            is WordEvent.OnGroupNameChanged -> state =
                state.copy(groupName = event.groupName)// TODO: groupName  değişkenini yeniden isimlendir
            WordEvent.OnBulkDelete -> bulkDeleteWords()
            WordEvent.OnBulkDeleteDialogVisibilityChanged -> state =
                state.copy(isBulkDeleteDialogVisible = !state.isBulkDeleteDialogVisible)

            is WordEvent.OnGroupToMoveChange -> state = state.copy(groupToMove = event.group)
            WordEvent.OnWordsMove -> moveWordsToGroup()
            WordEvent.OnWordsMoveDialogVisibilityChanged -> {
                val groups=state.groups.toMutableList()
                groups.remove(state.selectedGroup)
                if (groups.isEmpty()) {
                    viewModelScope.launch {
                        _eventFlow.emit(SnackBarEvent("Taşınacak bir gurup bulunamadı."))
                    }
                    return
                }
                state = state.copy(isWordsMoveDialogVisible = !state.isWordsMoveDialogVisible)
            }

            WordEvent.OnDeleteAllWordsCheckChanged -> state = state.copy(isDeleteAllWordsInGroupSelected = !state.isDeleteAllWordsInGroupSelected)
            WordEvent.OnDeleteGroupDialogVisibilityChanged -> state = state.copy(isDeleteGroupDialogVisible = !state.isDeleteGroupDialogVisible, editedGroup = null)
            is WordEvent.OnEditedGroupChanged -> state = state.copy(editedGroup = event.group)
            WordEvent.OnGroupDeleted -> deleteGroup()
            WordEvent.OnGroupUpdated -> updateGroupName()
            is WordEvent.OnNewGroupNameChanged -> state = state.copy(newGroupName = event.newGroupName)
            WordEvent.OnUpdatedGroupDialogVisibilityChanged -> state = state.copy(
                isUpdateGroupDialogVisible = !state.isUpdateGroupDialogVisible,
                editedGroup = null,
                newGroupName = "",
                newGroupNameError = null
            )
        }
    }

    private fun updateGroupName() {
        var editedGroup=state.editedGroup ?: return
        val newGroupName=state.newGroupName
        if (newGroupName.isBlank()) {
            state = state.copy(newGroupNameError = "Gurup adı boş olamaz.")
            return
        }
        if (newGroupName==editedGroup.groupName){
            state = state.copy(newGroupNameError = "Yeni gurup adı öncekiyle aynı olamaz.")
            return
        }
        if (state.groups.map { it.groupName }.contains(newGroupName)) {
            state = state.copy(
                newGroupNameError = "Zaten bu ada sahip bir gurup mevcut.",
            )
            return
        }
        state = state.copy(isLoading = true, isUpdateGroupDialogVisible = false)
        viewModelScope.launch {
            editedGroup=editedGroup.copy(version = editedGroup.version+1, groupName = newGroupName)
            setGroupToFirestore(editedGroup)
            updateGroupFromRoom(editedGroup)
            state = state.copy(isLoading = false)
            _eventFlow.emit(SnackBarEvent("Gurup adı değiştirildi."))
        }

    }

    private fun deleteGroup() {
        val deletedGroup=state.editedGroup ?: return
        state = state.copy(isLoading = true, isDeleteGroupDialogVisible = false)
        val isAllWordDeleted=state.isDeleteAllWordsInGroupSelected
        if (isAllWordDeleted){
            viewModelScope.launch {
                val wordsInGroup=  async { getWordsFromRoomByGroupId(groupId = deletedGroup.id).first()}
                wordsInGroup.await().forEach {word->
                    deleteWordFromRoom(word)
                    deleteWordFromFirestore(word)
                }
                launch {
                    deleteGroupFromRoom(deletedGroup)
                    deleteGroupFromFirestore(deletedGroup)
                }
                state = state.copy(
                    isLoading = false,
                    editedGroup = null,
                    selectedGroup = null
                )
                getWords(state.wordOrder)
                _eventFlow.emit(SnackBarEvent("Gurup ve kelimeler başarılı bir şekilde silindi."))
            }
            return
        }
        viewModelScope.launch {
            val wordsInGroup=  async { getWordsFromRoomByGroupId(groupId = deletedGroup.id).first()}
            wordsInGroup.await().forEach {
                val updatedWord=it.copy(version = it.version+1, groupId = null)
                updateWordInRoom(updatedWord)
                setWordToFirestore(updatedWord)
            }
            launch {
                deleteGroupFromRoom(deletedGroup)
                deleteGroupFromFirestore(deletedGroup)
            }
            state = state.copy(
                isLoading = false,
                editedGroup = null,
                selectedGroup = null,
            )
            getWords(state.wordOrder)
            _eventFlow.emit(SnackBarEvent("Gurup başarılı bir şekilde silindi."))
        }
    }

    private fun moveWordsToGroup() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, isWordsMoveDialogVisible = false)
            state.selectedWords.forEach {
                val updatedWord =
                    it.copy(groupId = state.groupToMove!!.id, version = it.version + 1)
                updateWordInRoom(updatedWord)
                setWordToFirestore(updatedWord)
            }
            state = state.copy(
                isLoading = false,
                selectedWords = emptyList(),
                selectedGroup = state.groupToMove,
                groupToMove = null,
                isLongClicked = false
            )
            getWords(state.wordOrder)
            _eventFlow.emit(SnackBarEvent("Kelimeler yeni guruba taşındı."))
        }
    }

    private fun bulkDeleteWords() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, isBulkDeleteDialogVisible = false)
            state.selectedWords.forEach {
                deleteWordFromRoom(it)
                deleteWordFromFirestore(it)
            }
            state = state.copy(isLoading = false, selectedWords = emptyList())
            _eventFlow.emit(SnackBarEvent("Kelimeler başarılı bir şekilde silindi."))
        }
    }

    private var addGroupJob: Job? = null
    private fun saveGroup() {
        if (state.groupName.isBlank()) {
            state = state.copy(
                groupNameError = "Gurup adı boş olmaz",
                groupName = ""
            )
            return
        }
        if (state.groups.map { it.groupName }.contains(state.groupName)) {
            state = state.copy(
                groupNameError = "Zaten bu ada sahip bir gurup mevcut.",
            )
            return
        }
        state = state.copy(
            isLoading = true,
            isGroupDialogVisible = false,
            groupNameError = null
        )
        addGroupJob?.cancel()
        addGroupJob = viewModelScope.launch {
            val group = Group(groupName = state.groupName)
            saveGroupToRoom(group)
            setGroupToFirestore(group)
            state = state.copy(isLoading = false)
            _eventFlow.emit(SnackBarEvent("${state.groupName} başarılı bir şekilde oluşturuldu."))
            state = state.copy(groupName = "", selectedGroup = group)
            getWords(state.wordOrder)

        }

    }

    private fun changeIsOpen() {
        state = state.copy(isSheetOpen = !state.isSheetOpen)
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
            state = state.copy(isUpdateDialogVisible = false)
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
            state = state.copy(isDeleteDialogVisible = false)
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
        getWords(state.wordOrder)
        getGroups()
    }

    private var wordJob: Job? = null

    private fun getWords(wordOrder: WordOrder) {
        wordJob?.cancel()
        wordJob = getWordsFromRoomByGroupId(wordOrder, state.selectedGroup?.id).onEach { words ->
            state = state.copy(wordList = words)
        }.launchIn(viewModelScope)

    }

    private var groupJob: Job? = null

    private fun getGroups() {
        groupJob?.cancel()
        groupJob = getGroupsFromRoom().onEach { groups ->
            state = state.copy(groups = groups)
        }.launchIn(viewModelScope)
    }

    private fun saveWord() {
        state = state.copy(isLoading = true, isAddDialogVisible = false)
        val wordResult = validateWord(state.word)
        val meaningResult = validateMeaning(state.meaning)

        if (wordResult.data != true) {
            state = state.copy(isLoading = false, isAddDialogVisible = true)
            state = state.copy(
                wordError = wordResult.message,
            )
            return
        }
        viewModelScope.launch {
            if (meaningResult.data != true) {

                state =
                    when (val result = translateWord(word = state.word.lowercase(Locale.ROOT))) {
                        is Resource.Error -> state.copy(
                            meaningError = result.message,
                            isLoading = false,
                            isAddDialogVisible = true
                        )

                        is Resource.Loading -> state.copy(isLoading = true)
                        is Resource.Success -> state.copy(
                            meaning = result.data!!.translatedWord,
                            isLoading = false
                        )
                    }

            }
            val word = Word(
                name = state.word.lowercase(Locale.ROOT),
                meaning = state.meaning.lowercase(Locale.ROOT),
                groupId = state.selectedGroup?.id
            )
            saveWordToRoom(word)
            setWordToFirestore(word)
            state = state.copy(
                word = "",
                meaning = "",
                wordError = null,
                meaningError = null,
                isAddDialogVisible = false,
                isLoading = false
            )
        }
    }

    data class SnackBarEvent(val message: String)


}