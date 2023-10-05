package com.novacodestudios.recall.presentation.sign_up

import com.novacodestudios.recall.presentation.util.UIText

data class SignUpState(
    val fullName: String = "",
    val fullNameError: UIText? = null,
    val email: String = "",
    val emailError: UIText? = null,
    val password: String = "",
    val passwordError: UIText? = null,
    val repeatedPassword: String = "",
    val repeatedPasswordError: UIText? = null,
    val isLoading:Boolean=false
    )
