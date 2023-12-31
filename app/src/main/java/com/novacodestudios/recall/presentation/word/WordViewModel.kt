package com.novacodestudios.recall.presentation.word

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacodestudios.recall.R
import com.novacodestudios.recall.domain.model.Group
import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.use_case.DeleteGroupFromFirestore
import com.novacodestudios.recall.domain.use_case.DeleteGroupFromRoom
import com.novacodestudios.recall.domain.use_case.DeleteQuestionFromFirestore
import com.novacodestudios.recall.domain.use_case.DeleteQuestionFromRoom
import com.novacodestudios.recall.domain.use_case.DeleteWordFromFirestore
import com.novacodestudios.recall.domain.use_case.DeleteWordFromRoom
import com.novacodestudios.recall.domain.use_case.GetActiveGroupId
import com.novacodestudios.recall.domain.use_case.GetGroupsFromRoom
import com.novacodestudios.recall.domain.use_case.GetMeaningVisibility
import com.novacodestudios.recall.domain.use_case.GetQuestionFromActiveQuizzesByWordIdFromRoom
import com.novacodestudios.recall.domain.use_case.GetWordsBySearch
import com.novacodestudios.recall.domain.use_case.GetWordsFromRoomByGroupId
import com.novacodestudios.recall.domain.use_case.SaveGroupToRoom
import com.novacodestudios.recall.domain.use_case.SaveWordToRoom
import com.novacodestudios.recall.domain.use_case.SetActiveGroupId
import com.novacodestudios.recall.domain.use_case.SetGroupToFirestore
import com.novacodestudios.recall.domain.use_case.SetWordToFirestore
import com.novacodestudios.recall.domain.use_case.UpdateGroupFromRoom
import com.novacodestudios.recall.domain.use_case.UpdateWordInRoom
import com.novacodestudios.recall.domain.use_case.ValidateMeaning
import com.novacodestudios.recall.domain.use_case.ValidateWord
import com.novacodestudios.recall.domain.util.WordOrder
import com.novacodestudios.recall.presentation.util.UIText
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
 //   private val translateWord: TranslateWord,
    private val getGroupsFromRoom: GetGroupsFromRoom,
    private val saveGroupToRoom: SaveGroupToRoom,
    private val setGroupToFirestore: SetGroupToFirestore,
    private val deleteGroupFromRoom: DeleteGroupFromRoom,
    private val deleteGroupFromFirestore: DeleteGroupFromFirestore,
    private val updateGroupFromRoom: UpdateGroupFromRoom,
    private val getQuestionFromActiveQuizzesByWordIdFromRoom: GetQuestionFromActiveQuizzesByWordIdFromRoom,
    private val deleteQuestionFromFirestore: DeleteQuestionFromFirestore,
    private val deleteQuestionFromRoom: DeleteQuestionFromRoom,
    getMeaningVisibility: GetMeaningVisibility,
    getActiveGroupId: GetActiveGroupId,
    private val setActiveGroupId: SetActiveGroupId
) : ViewModel() {

    var state by mutableStateOf(WordState())

    private val _eventFlow = MutableSharedFlow<UIText>()
    val eventFlow: SharedFlow<UIText> = _eventFlow.asSharedFlow()

    fun onEvent(event: WordEvent) {
        when (event) {
            is WordEvent.OnMeaningChanged -> state = state.copy(meaning = event.meaning)
            WordEvent.SaveWord -> saveWord()
            is WordEvent.OnWordChanged -> state = state.copy(word = event.word)
            is WordEvent.OnSearchChanged -> searchWorld(event.search)
            is WordEvent.OnAddDialogVisibilityChange -> {
                state = state.copy(
                    isAddDialogVisible = event.isVisible,
                    wordError = null,
                    meaningError = null
                )
            }

            WordEvent.DeleteWord -> deleteWord()
            WordEvent.UpdateWord -> updateWord()
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
                viewModelScope.launch { setActiveGroupId(event.group?.id ?: -1) }
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
                val groups = state.groups.toMutableList()
                groups.remove(state.selectedGroup)
                if (groups.isEmpty()) {
                    viewModelScope.launch {
                        _eventFlow.emit(UIText.StringResource(R.string.move_no_group_found))
                    }
                    return
                }
                state = state.copy(isWordsMoveDialogVisible = !state.isWordsMoveDialogVisible)
            }

            WordEvent.OnDeleteAllWordsCheckChanged -> state =
                state.copy(isDeleteAllWordsInGroupSelected = !state.isDeleteAllWordsInGroupSelected)

            WordEvent.OnDeleteGroupDialogVisibilityChanged -> state = state.copy(
                isDeleteGroupDialogVisible = !state.isDeleteGroupDialogVisible,
                editedGroup = null
            )

            is WordEvent.OnEditedGroupChanged -> state = state.copy(editedGroup = event.group)
            WordEvent.OnGroupDeleted -> deleteGroup()
            WordEvent.OnGroupUpdated -> updateGroupName()
            is WordEvent.OnNewGroupNameChanged -> state =
                state.copy(newGroupName = event.newGroupName)

            WordEvent.OnUpdatedGroupDialogVisibilityChanged -> state = state.copy(
                isUpdateGroupDialogVisible = !state.isUpdateGroupDialogVisible,
                editedGroup = null,
                newGroupName = "",
                newGroupNameError = null
            )
        }
    }

    private fun updateGroupName() {
        var editedGroup = state.editedGroup ?: return
        val newGroupName = state.newGroupName
        if (newGroupName.isBlank()) {
            state =
                state.copy(newGroupNameError = UIText.StringResource(R.string.group_name_cannot_be_empty))
            return
        }
        if (newGroupName == editedGroup.groupName) {
            state =
                state.copy(newGroupNameError = UIText.StringResource(R.string.group_update_redundant))
            return
        }
        if (state.groups.map { it.groupName }.contains(newGroupName)) {
            state = state.copy(
                newGroupNameError = UIText.StringResource(R.string.group_name_already_exists),
            )
            return
        }
        state = state.copy(isLoading = true, isUpdateGroupDialogVisible = false)
        viewModelScope.launch {
            editedGroup =
                editedGroup.copy(version = editedGroup.version + 1, groupName = newGroupName)
            setGroupToFirestore(editedGroup)
            updateGroupFromRoom(editedGroup)
            state = state.copy(isLoading = false)
            _eventFlow.emit(UIText.StringResource(R.string.group_name_update_success))
        }

    }

    private fun deleteQuestionsInActiveQuizzes(wordId: Int) {
        viewModelScope.launch {
            val questions =
                async { getQuestionFromActiveQuizzesByWordIdFromRoom(wordId).first() }
            questions.await().forEach { question ->
                launch { deleteQuestionFromFirestore(question) }
                launch { deleteQuestionFromRoom(question) }
            }
        }
    }

    private fun deleteGroup() {
        val deletedGroup = state.editedGroup ?: return
        state = state.copy(isLoading = true, isDeleteGroupDialogVisible = false)
        val isAllWordDeleted = state.isDeleteAllWordsInGroupSelected
        if (isAllWordDeleted) {
            val deleteJob = viewModelScope.launch {
                val wordsInGroup =
                    async { getWordsFromRoomByGroupId(groupId = deletedGroup.id).first() }
                wordsInGroup.await().forEach { word ->
                    launch { deleteWordFromRoom(word) }
                    launch { deleteWordFromFirestore(word) }
                    deleteQuestionsInActiveQuizzes(word.id)
                }
                launch { deleteGroupFromRoom(deletedGroup) }
                launch { deleteGroupFromFirestore(deletedGroup) }
            }
            viewModelScope.launch {
                deleteJob.join()
                state = state.copy(
                    isLoading = false,
                    editedGroup = null,
                    selectedGroup = null
                )
                getWords(state.wordOrder)
                _eventFlow.emit(UIText.StringResource(R.string.group_words_deleted_success))
            }
            return
        }
        val deleteJob = viewModelScope.launch {
            val wordsInGroup =
                async { getWordsFromRoomByGroupId(groupId = deletedGroup.id).first() }
            wordsInGroup.await().forEach {
                val updatedWord = it.copy(version = it.version + 1, groupId = null)
                launch { updateWordInRoom(updatedWord) }
                launch { setWordToFirestore(updatedWord) }
            }
            launch { deleteGroupFromRoom(deletedGroup) }
            launch { deleteGroupFromFirestore(deletedGroup) }
        }
        viewModelScope.launch {
            deleteJob.join()
            state = state.copy(
                isLoading = false,
                editedGroup = null,
                selectedGroup = null,
            )
            getWords(state.wordOrder)
            _eventFlow.emit(UIText.StringResource(R.string.group_deleted_success))
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
            _eventFlow.emit(UIText.StringResource(R.string.words_moved_success))
        }
    }

    private fun bulkDeleteWords() {
        val bulkDeleteJob = viewModelScope.launch {
            state = state.copy(isLoading = true, isBulkDeleteDialogVisible = false)
            state.selectedWords.forEach { word ->
                launch { deleteWordFromRoom(word) }
                launch { deleteWordFromFirestore(word) }
                deleteQuestionsInActiveQuizzes(word.id)
            }
        }
        viewModelScope.launch {
            bulkDeleteJob.join()
            state =
                state.copy(isLoading = false, selectedWords = emptyList(), isLongClicked = false)
            _eventFlow.emit(UIText.StringResource(R.string.words_deleted_success))
        }
    }

    private var addGroupJob: Job? = null
    private fun saveGroup() {
        if (state.groupName.isBlank()) {
            state = state.copy(
                groupNameError = UIText.StringResource(R.string.group_name_cannot_be_empty),
                groupName = ""
            )
            return
        }
        if (state.groups.map { it.groupName }.contains(state.groupName)) {
            state = state.copy(
                groupNameError = UIText.StringResource(R.string.group_name_already_exists),
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
            _eventFlow.emit(UIText.StringResource(R.string.group_created_success))
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
            state = state.copy(isLoading = true, isUpdateDialogVisible = false)
            val updatedWord = state.updatedWord!!.copy(
                name = state.word,
                meaning = state.meaning,
                version = state.updatedWord!!.version + 1,
            )
            updateWordInRoom(updatedWord)
            setWordToFirestore(updatedWord)
            state = state.copy(
                word = "",
                meaning = "",
                wordError = null,
                meaningError = null,
                updatedWord = null,
                isLoading = false
            )
        }

    }

    private fun isRedundantUpdate(
        newName: String,
        newMeaning: String,
        oldWord: Word
    ): Resource<Boolean> {
        if (oldWord.name == newName && oldWord.meaning == newMeaning)
            return Resource.Error(message = UIText.StringResource(R.string.word_update_redundant))

        return Resource.Success(true)
    }

    private fun deleteWord() {
        state = state.copy(isDeleteDialogVisible = false, isLoading = true)
        val deleteJob = viewModelScope.launch {
            val deletedWord = state.deletedWord ?: return@launch
            launch { deleteWordFromRoom(deletedWord) }
            launch { deleteWordFromFirestore(deletedWord) }
            deleteQuestionsInActiveQuizzes(deletedWord.id)
        }
        viewModelScope.launch {
            deleteJob.join()
            state = state.copy(deletedWord = null, isLoading = false)
            _eventFlow.emit(UIText.StringResource(R.string.word_deleted_success))
        }
    }

    private var searchJob: Job? = null

    private fun searchWorld(search: String) {
        state = state.copy(search = search)
        searchJob?.cancel()
        searchJob = getWordsBySearch(state.search, state.selectedGroup?.id).onEach { words ->
            state = state.copy(wordList = words)
            delay(500L)
        }.launchIn(viewModelScope)


    }

    private var visibilityJob: Job? = null
    private var activeGroupJob: Job? = null
    private var groupsJob: Job? = null

    init {
        visibilityJob?.cancel()
        visibilityJob = getMeaningVisibility().onEach {
            state = state.copy(isMeaningVisible = it)
        }.launchIn(viewModelScope)

        groupsJob?.cancel()
        groupsJob = getGroupsFromRoom().onEach { groups ->
            state = state.copy(groups = groups)
        }.launchIn(viewModelScope)

        activeGroupJob?.cancel()
        activeGroupJob = getActiveGroupId().onEach { activeGroupId ->
            val activeGroup = getGroupsFromRoom().first().find { it.id == activeGroupId }
            state = state.copy(selectedGroup = activeGroup)
            getWords(state.wordOrder)
        }.launchIn(viewModelScope)


    }

    private var wordJob: Job? = null

    private fun getWords(wordOrder: WordOrder) {
        wordJob?.cancel()
        wordJob = getWordsFromRoomByGroupId(wordOrder, state.selectedGroup?.id).onEach { words ->
            state = state.copy(wordList = words)
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
        if (meaningResult.data!=true){
            state = state.copy(isLoading = false, isAddDialogVisible = true)
            state = state.copy(
                meaningError = meaningResult.message
            )
            return
        }
        viewModelScope.launch {

            // TODO: Transle özelliği sonra eklenecek
            /*if (meaningResult.data != true) {

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

            }*/
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


}