package com.novacodestudios.recall

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route:String, val icon:ImageVector?=null,val title:String?=null){
    object SignUpScreen: Screen("sign_up_screen")
    object SignInScreen: Screen("sign_in_screen")
    object SettingsScreen: Screen("settings_screen", icon = Icons.Filled.Settings, title = "Ayarlar")
    object WordScreen: Screen("word_screen",icon = Icons.Filled.ListAlt, title = "Kelimeler")
    object QuizScreen: Screen("quiz_screen")
    object ResultScreen: Screen("result_screen")
    object QuizHistory: Screen("quiz_history_screen", icon = Icons.Filled.Quiz,title = "Sınavlar")
}
