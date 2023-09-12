package com.novacodestudios.recall.presentation.util

sealed class Screen(val route:String){
    data object SignUpScreen: Screen("sign_up_screen")
    data object SignInScreen: Screen("sign_in_screen")
    data object SettingsScreen: Screen("settings_screen")
    data object WordScreen: Screen("word_screen")
    data object QuestionScreen: Screen("question_screen")
    data object ResultScreen: Screen("result_screen")
    data object QuizHistory: Screen("quiz_history_screen")
}
