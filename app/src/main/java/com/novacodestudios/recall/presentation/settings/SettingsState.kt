package com.novacodestudios.recall.presentation.settings

import com.novacodestudios.recall.presentation.util.UIText

data class SettingsState(
    val isDarkTheme:Boolean=false,
    val isLoading:Boolean=false,
    val isMeaningVisible:Boolean=true,
    val isDeleteDialogVisible:Boolean=false,
    val email: String = "",
    val emailError: UIText? = null,
    val password: String = "",
    val passwordError: UIText? = null,
)
