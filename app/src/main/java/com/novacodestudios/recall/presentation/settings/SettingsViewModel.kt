package com.novacodestudios.recall.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacodestudios.recall.R
import com.novacodestudios.recall.domain.use_case.DeleteAllGroups
import com.novacodestudios.recall.domain.use_case.DeleteAllGroupsInFirestore
import com.novacodestudios.recall.domain.use_case.DeleteAllQuestions
import com.novacodestudios.recall.domain.use_case.DeleteAllQuestionsInFirestore
import com.novacodestudios.recall.domain.use_case.DeleteAllQuizzes
import com.novacodestudios.recall.domain.use_case.DeleteAllQuizzesInFirestore
import com.novacodestudios.recall.domain.use_case.DeleteAllWords
import com.novacodestudios.recall.domain.use_case.DeleteAllWordsInFirestore
import com.novacodestudios.recall.domain.use_case.DeleteUserAccount
import com.novacodestudios.recall.domain.use_case.GetMeaningVisibility
import com.novacodestudios.recall.domain.use_case.GetTheme
import com.novacodestudios.recall.domain.use_case.ReAuthenticateUser
import com.novacodestudios.recall.domain.use_case.SetMeaningVisibility
import com.novacodestudios.recall.domain.use_case.SetTheme
import com.novacodestudios.recall.domain.use_case.SignOutUserFromFirebase
import com.novacodestudios.recall.domain.use_case.ValidateEmail
import com.novacodestudios.recall.domain.use_case.ValidatePassword
import com.novacodestudios.recall.presentation.util.UIText
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
    private val deleteAllQuestions: DeleteAllQuestions,
    getMeaningVisibility: GetMeaningVisibility,
    private val setMeaningVisibility: SetMeaningVisibility,
    private val deleteUserAccount: DeleteUserAccount,
    private val reAuthenticateUser: ReAuthenticateUser,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val deleteAllGroups: DeleteAllGroups,
    private val deleteAllWordsInFirestore: DeleteAllWordsInFirestore,
    private val deleteAllQuizzesInFirestore: DeleteAllQuizzesInFirestore,
    private val deleteAllQuestionsInFirestore: DeleteAllQuestionsInFirestore,
    private val deleteAllGroupsInFirestore: DeleteAllGroupsInFirestore,
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
            is SettingsEvent.OnMeaningVisibilityChanged -> changeMeaningVisibility(event.isVisible)
            is SettingsEvent.EmailChanged -> state = state.copy(email = event.email)
            is SettingsEvent.PasswordChanged -> state = state.copy(password = event.password)
            SettingsEvent.DialogVisibilityChanged -> state = state.copy(
                isDeleteDialogVisible = !state.isDeleteDialogVisible,
                email = "",
                emailError = null,
                password = "",
                passwordError = null
            )
        }
    }


    private var job: Job? = null
    private var visibilityJob: Job? = null

    init {
        job?.cancel()
        job = getTheme().onEach {
            state = state.copy(isDarkTheme = it)
        }.launchIn(viewModelScope)

        visibilityJob?.cancel()
        visibilityJob = getMeaningVisibility().onEach { isVisible ->
            state = state.copy(isMeaningVisible = isVisible)
        }.launchIn(viewModelScope)

    }

    //best practice: VM fonksiyonlarını suspend yapma onun yerine viewModelScope kullan.
    private fun changeTheme(isDarkMode: Boolean) {
        viewModelScope.launch {
            setTheme(isDarkMode)
        }

    }

    private fun changeMeaningVisibility(isVisible: Boolean) {
        viewModelScope.launch {
            setMeaningVisibility(isVisible)
        }
    }

    private fun deleteAccount() {
        val emailResult = validateEmail(state.email)
        val passwordResult = validatePassword(state.password)
        val hasError = listOf(
            emailResult, passwordResult
        ).any { it.data != true }

        if (hasError) {
            state = state.copy(
                emailError = emailResult.message,
                passwordError = passwordResult.message
            )
            return
        }
        viewModelScope.launch {
            state = state.copy(isLoading = true, isDeleteDialogVisible = false)

            try {
                reAuthenticateUser(state.email, state.password)
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false, email = "",
                    emailError = null,
                    password = "",
                    passwordError = null
                )
                _eventFlow.emit(UIEvent.ShowSnackbar(UIText.DynamicText(e.localizedMessage!!)))
                return@launch
            }
            try {
                deleteUserAccount()
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false, email = "",
                    emailError = null,
                    password = "",
                    passwordError = null
                )
                _eventFlow.emit(UIEvent.ShowSnackbar(UIText.DynamicText(e.localizedMessage!!)))
                return@launch
            }
            deleteAllTables()
            state = state.copy(
                isLoading = false,
                email = "",
                emailError = null,
                password = "",
                passwordError = null
            )

            _eventFlow.emit(UIEvent.ShowSnackbar(UIText.StringResource(R.string.delete_account_success)))
            _eventFlow.emit(UIEvent.SignOut)
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            signOutUserFromFirebase()
            state = state.copy(isLoading = false)
            _eventFlow.emit(UIEvent.SignOut)
        }
    }

    private fun deleteAllTables() {
        val job = viewModelScope.launch {
            launch { deleteAllWords() }
            launch { deleteAllQuizzes() }
            launch { deleteAllQuestions() }
            launch { deleteAllGroups() }
        }
        viewModelScope.launch {
            job.join()
        }
    }

    private fun deleteAllCollections() {
        try {
            val job = viewModelScope.launch {
                launch {  deleteAllWordsInFirestore()}
                launch {  deleteAllQuizzesInFirestore()}
                launch {  deleteAllQuestionsInFirestore()}
                launch {  deleteAllGroupsInFirestore()}
            }

            viewModelScope.launch {
                job.join()
            }
        } catch (e: Exception) {
            viewModelScope.launch{
                _eventFlow.emit(UIEvent.ShowSnackbar(UIText.DynamicText(e.localizedMessage!!)))
            }
        }


    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: UIText) : UIEvent()
        data object SignOut : UIEvent()
    }

}