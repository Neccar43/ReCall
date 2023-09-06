package com.novacodestudios.recall.presentation.navigation

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.novacodestudios.recall.presentation.quiz_history.QuizHistoryScreen
import com.novacodestudios.recall.presentation.settings.SettingsScreen
import com.novacodestudios.recall.presentation.util.Screen
import com.novacodestudios.recall.presentation.word.WordScreen

fun NavGraphBuilder.mainGraph(navController: NavController, context: Context) {
    navigation(route = Graph.MAIN, startDestination = Screen.WordScreen.route) {
        composable(Screen.WordScreen.route,
            ) {
            WordScreen()
        }
        composable(Screen.QuizHistory.route) {
            QuizHistoryScreen(
                context = context,
                onNavigateToQuestionScreen = {
                    navController.navigate(Screen.QuestionScreen.route + "/$it")

                },
                onNavigateToResultScreen = {
                    navController.navigate(Screen.ResultScreen.route + "/$it")
                }
            )
        }
        composable(Screen.SettingsScreen.route) {
            SettingsScreen(onNavigateToAuthGraph = {
                navController.navigate(Graph.AUTH) {
                    popUpTo(Graph.MAIN)
                }
            })
        }
    }
}
