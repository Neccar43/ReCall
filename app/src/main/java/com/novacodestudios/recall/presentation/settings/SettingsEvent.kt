package com.novacodestudios.recall.presentation.settings

sealed class SettingsEvent {
    data object SignOut : SettingsEvent()
    data class ThemeChanged(val isDarkMode:Boolean) : SettingsEvent()
    data class OnMeaningVisibilityChanged(val isVisible:Boolean):SettingsEvent()
    data object DeleteAccount:SettingsEvent()
    data class EmailChanged(val email:String): SettingsEvent()
    data class PasswordChanged(val password:String): SettingsEvent()
    data object DialogVisibilityChanged:SettingsEvent()
}
