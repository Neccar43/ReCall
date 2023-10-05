package com.novacodestudios.recall.presentation.sign_in

import com.novacodestudios.recall.presentation.util.UIText

data class SignInState(
    val email: String = "",
    val emailError: UIText? = null,
    val password: String = "",
    val passwordError: UIText? = null,
    val isLoading:Boolean=false,
    val isDialogVisible:Boolean=false,
    val forgetEmail:String="",
    val forgetEmailError: UIText?=null
)
