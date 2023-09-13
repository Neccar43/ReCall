package com.novacodestudios.recall.presentation.sign_up

data class SignUpState(
    val name: String = "",
    val nameError: String? = null,
    val surname: String = "",
    val surnameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val repeatedPassword: String = "",
    val repeatedPasswordError: String? = null,
    val isLoading:Boolean=false
    )
