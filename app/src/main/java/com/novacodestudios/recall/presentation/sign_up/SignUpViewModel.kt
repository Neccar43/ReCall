package com.novacodestudios.recall.presentation.sign_up

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacodestudios.recall.domain.use_case.SignUpUserToFirebase
import com.novacodestudios.recall.domain.use_case.ValidateEmail
import com.novacodestudios.recall.domain.use_case.ValidateName
import com.novacodestudios.recall.domain.use_case.ValidatePassword
import com.novacodestudios.recall.domain.use_case.ValidateRepeatedPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val validateName: ValidateName,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validateRepeatedPassword: ValidateRepeatedPassword,
    private val signUpUserToFirebase: SignUpUserToFirebase,

    ) : ViewModel() {

    var state by mutableStateOf(SignUpState())

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.EmailChanged -> state = state.copy(email = event.email)
            is SignUpEvent.FullNameChanged -> state = state.copy(fullName = event.fullName)
            is SignUpEvent.PasswordChanged -> state = state.copy(password = event.password)
            is SignUpEvent.RepeatedPasswordChanged -> state =
                state.copy(repeatedPassword = event.repeatedPassword)

            is SignUpEvent.SignUp -> signUp()
        }
    }

    private fun signUp() {
        state=state.copy(isLoading = true)
        val nameResult = validateName(state.fullName)
        val emailResult = validateEmail(state.email)
        val passwordResult = validatePassword(state.password)
        val repeatedPasswordResult =
            validateRepeatedPassword(state.password, state.repeatedPassword)

        val hasError = listOf(
            nameResult,
            emailResult,
            passwordResult,
            repeatedPasswordResult
        ).any { it.data != true }

        if (hasError) {
            state=state.copy(isLoading = false)
            state = state.copy(
                fullNameError = nameResult.message,
                emailError = emailResult.message,
                passwordError = passwordResult.message,
                repeatedPasswordError = repeatedPasswordResult.message
            )
            return
        }
        viewModelScope.launch {
            try {
                signUpUserToFirebase(state.email, state.password)
                state=state.copy(isLoading = false)
                _eventFlow.emit(UIEvent.SignUp)
            } catch (e: Exception) {
                state=state.copy(isLoading = false)
                _eventFlow.emit(UIEvent.ShowSnackbar(e.localizedMessage!!))
            }
        }

    }
    sealed class UIEvent{
        data class ShowSnackbar(val message:String):UIEvent()
        data object SignUp:UIEvent()
    }

}

