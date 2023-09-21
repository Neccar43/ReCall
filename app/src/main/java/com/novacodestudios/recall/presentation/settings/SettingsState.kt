package com.novacodestudios.recall.presentation.settings

data class SettingsState(
    val isDarkTheme:Boolean=false,
    val isLoading:Boolean=false,
    val isMeaningVisible:Boolean=true
)
