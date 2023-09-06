package com.novacodestudios.recall.presentation.settings

sealed class SettingsEvent {
    data object SignOut : SettingsEvent()
    data class ThemeChanged(val isDarkMode:Boolean) : SettingsEvent()
    data object DeleteAccount:SettingsEvent()

}
