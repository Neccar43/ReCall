package com.novacodestudios.recall.presentation.sign_up

sealed class SignUpEvent{
    data class FullNameChanged(val fullName:String):SignUpEvent()
    data class EmailChanged(val email:String):SignUpEvent()
    data class PasswordChanged(val password:String):SignUpEvent()
    data class RepeatedPasswordChanged(val repeatedPassword:String):SignUpEvent()
    data object SignUp:SignUpEvent()
}
