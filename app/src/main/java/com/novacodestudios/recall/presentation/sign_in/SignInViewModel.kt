package com.novacodestudios.recall.presentation.sign_in

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacodestudios.recall.domain.use_case.SignInUserWithFirebase
import com.novacodestudios.recall.domain.use_case.SyncDataWorkerUseCase
import com.novacodestudios.recall.domain.use_case.ValidateEmail
import com.novacodestudios.recall.domain.use_case.ValidatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val signInUserWithFirebase: SignInUserWithFirebase,
    private val  syncDataWorkerUseCase: SyncDataWorkerUseCase
) : ViewModel() {
    var state by mutableStateOf(SignInState())

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.EmailChanged -> state = state.copy(email = event.email)
            is SignInEvent.PasswordChanged -> state = state.copy(password = event.password)
            is SignInEvent.SignIn -> signIn()
        }
    }

    private fun signIn() {
        state=state.copy(isLoading = true)

        val emailResult = validateEmail(state.email)
        val passwordResult = validatePassword(state.password)


        val hasError = listOf(
            emailResult,
            passwordResult,
        ).any { it.data != true }
        if (hasError) {
            state = state.copy(
                emailError = emailResult.message,
                passwordError = passwordResult.message,
            )
            state=state.copy(isLoading = false)
            return
        }
        viewModelScope.launch {
            try {
                signInUserWithFirebase(state.email, state.password)
                state=state.copy(isLoading = false)
                _eventFlow.emit(UIEvent.SignIn)
                syncDataWorkerUseCase()
            } catch (e: Exception) {
                state=state.copy(isLoading = false)
                _eventFlow.emit(UIEvent.ShowSnackbar(e.localizedMessage!!))
            }
        }
    }



    sealed class UIEvent{
        data class ShowSnackbar(val message:String):UIEvent()
        data object SignIn:UIEvent()
    }
}