package com.novacodestudios.recall.presentation.sign_in

sealed class SignInEvent{
    data class EmailChanged(val email:String):SignInEvent()
    data class PasswordChanged(val password:String):SignInEvent()
    data object SignIn:SignInEvent()
    data object ForgetPasswordDialogVisibilityChanged:SignInEvent()
    data class ForgetPassWordEmailChanged(val email: String):SignInEvent()
    data object SendPasswordResetEmail:SignInEvent()
}
