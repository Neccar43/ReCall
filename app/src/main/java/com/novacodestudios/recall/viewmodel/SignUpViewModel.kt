package com.novacodestudios.recall.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.novacodestudios.recall.repository.ReCallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(repository: ReCallRepository) : ViewModel() {

    private val _state = mutableStateOf(SignUpState())
    val state: State<SignUpState> = _state

    fun signUpUser(
        name: String,
        surname: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _state.value = SignUpState(error = "Alanlar boş olamaz.")
        } else if (password.length < 6 || confirmPassword.length < 6) {
            _state.value = SignUpState(error = "Şifre en az 6 haneli olmalı.")
        } else if (password != confirmPassword) {
            _state.value = SignUpState(error = "Şifreler uyuşmuyor.")
        }
    }

}

data class SignUpState(
    val error: String = "",
    val isLoading: Boolean = false,
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""

)