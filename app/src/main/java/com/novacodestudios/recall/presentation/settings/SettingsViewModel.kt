package com.novacodestudios.recall.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacodestudios.recall.domain.use_case.DeleteAllQuestions
import com.novacodestudios.recall.domain.use_case.DeleteAllQuizzes
import com.novacodestudios.recall.domain.use_case.DeleteAllWords
import com.novacodestudios.recall.domain.use_case.GetTheme
import com.novacodestudios.recall.domain.use_case.SetTheme
import com.novacodestudios.recall.domain.use_case.SignOutUserFromFirebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val signOutUserFromFirebase: SignOutUserFromFirebase,
    private val setTheme: SetTheme,
     getTheme: GetTheme,
    private val deleteAllWords: DeleteAllWords,
    private val deleteAllQuizzes: DeleteAllQuizzes,
    private val deleteAllQuestions: DeleteAllQuestions
) : ViewModel() {

    var state by mutableStateOf(SettingsState())

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

     fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.SignOut -> {
                signOut()
                deleteAllTables()
            }
            is SettingsEvent.ThemeChanged -> changeTheme(event.isDarkMode)
            is SettingsEvent.DeleteAccount -> deleteAccount()
        }
    }

    private var job: Job? = null

    init {
        job?.cancel()
        job = getTheme().onEach {
            state = state.copy(isDarkTheme = it)
        }.launchIn(viewModelScope)

    }
    //best practice: VM fonksiyonlarını suspend yapma onun yerine viewModelScope kullan.
    private  fun changeTheme(isDarkMode: Boolean) {
        viewModelScope.launch {
            setTheme(isDarkMode)
        }

    }

    private fun deleteAccount() {
        // TODO: Burayı sonradan ekle
    }

    private  fun signOut() {
        viewModelScope.launch {
            state=state.copy(isLoading = true)
            signOutUserFromFirebase()
            state=state.copy(isLoading = false)
            _eventFlow.emit(UIEvent.SignOut)
        }
    }
    private fun deleteAllTables(){
        viewModelScope.launch {
            launch { deleteAllWords() }
            launch { deleteAllQuizzes() }
            launch {deleteAllQuestions()  }
        }
    }

    sealed class UIEvent{
        data object SignOut:UIEvent()
    }

}