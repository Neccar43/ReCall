package com.novacodestudios.recall.presentation.sign_in

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacodestudios.recall.R
import com.novacodestudios.recall.domain.use_case.SendResetPasswordEmail
import com.novacodestudios.recall.domain.use_case.SignInUserWithFirebase
import com.novacodestudios.recall.domain.use_case.SyncDataWorkerUseCase
import com.novacodestudios.recall.domain.use_case.ValidateEmail
import com.novacodestudios.recall.domain.use_case.ValidatePassword
import com.novacodestudios.recall.presentation.util.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val signInUserWithFirebase: SignInUserWithFirebase,
    private val syncDataWorkerUseCase: SyncDataWorkerUseCase,
    private val sendResetPasswordEmail: SendResetPasswordEmail,
) : ViewModel() {
    var state by mutableStateOf(SignInState())

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.EmailChanged -> state = state.copy(email = event.email)
            is SignInEvent.PasswordChanged -> state = state.copy(password = event.password)
            is SignInEvent.SignIn -> signIn()
            is SignInEvent.ForgetPassWordEmailChanged -> state =
                state.copy(forgetEmail = event.email)

            SignInEvent.ForgetPasswordDialogVisibilityChanged -> state = state.copy(
                isDialogVisible = !state.isDialogVisible,
                forgetEmail = "",
                forgetEmailError = null
            )

            SignInEvent.SendPasswordResetEmail -> sendPasswordResetEmail()
        }
    }

    private fun sendPasswordResetEmail() {
        if (state.forgetEmail.isBlank()) {
            state =
                state.copy(forgetEmailError = UIText.StringResource(R.string.email_cannot_blank))
            return
        }
        viewModelScope.launch {
            state = state.copy(isDialogVisible = false, isLoading = true)
            try {
                sendResetPasswordEmail(state.forgetEmail)
            } catch (e: Exception) {
                _eventFlow.emit(UIEvent.ShowSnackbar(UIText.DynamicText(e.localizedMessage!!)))
                state = state.copy(isLoading = false, forgetEmail = "", forgetEmailError = null)
                return@launch
            }
            state = state.copy(isLoading = false, forgetEmail = "", forgetEmailError = null)
            _eventFlow.emit(UIEvent.ShowSnackbar(UIText.StringResource(R.string.reset_password_email_success)))
        }


    }

    private fun signIn() {
        state = state.copy(isLoading = true)

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
            state = state.copy(isLoading = false)
            return
        }
        viewModelScope.launch {
            try {
                withTimeout(15000){
                    signInUserWithFirebase(state.email, state.password)
                }

                state = state.copy(isLoading = false)
                _eventFlow.emit(UIEvent.SignIn)
                syncDataWorkerUseCase()
            }catch (e: TimeoutCancellationException) {
                state = state.copy(isLoading = false)
                _eventFlow.emit(UIEvent.ShowSnackbar(UIText.StringResource(R.string.auth_time_out)))
            }
            catch (e: Exception) {
                state = state.copy(isLoading = false)
                _eventFlow.emit(UIEvent.ShowSnackbar(UIText.DynamicText(e.localizedMessage!!)))
            }
        }
    }


    sealed class UIEvent {
        data class ShowSnackbar(val message: UIText) : UIEvent()
        data object SignIn : UIEvent()
    }
}