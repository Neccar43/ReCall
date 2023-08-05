package com.novacodestudios.recall.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.novacodestudios.recall.presentation.util.Screen
import com.novacodestudios.recall.ui.theme.ReCallTheme
import com.novacodestudios.recall.presentation.quiz_history.QuizHistoryScreen
import com.novacodestudios.recall.presentation.quiz.QuizScreen
import com.novacodestudios.recall.presentation.quiz_result.QuizResultScreen
import com.novacodestudios.recall.presentation.settings.SettingsScreen
import com.novacodestudios.recall.presentation.sign_in.SignInScreen
import com.novacodestudios.recall.presentation.sign_up.SignUpScreen
import com.novacodestudios.recall.presentation.word.WordScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navItems = listOf(Screen.WordScreen, Screen.QuizHistory, Screen.SettingsScreen)
            ReCallTheme{

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    Scaffold(

                        bottomBar = {
                            if (currentDestination != null) {
                                when (currentDestination.route) {
                                    Screen.SettingsScreen.route , Screen.WordScreen.route, Screen.QuizHistory.route-> {
                                        BottomNavigation {
                                            navItems.forEach { screen ->
                                                BottomNavigationItem(
                                                    icon = {
                                                        screen.icon?.let {
                                                            Icon(
                                                                it,
                                                                contentDescription = null
                                                            )
                                                        }
                                                    },
                                                    label = { screen.title?.let { Text(text = it) } },
                                                    selected = currentDestination.hierarchy.any { it.route == screen.route },
                                                    alwaysShowLabel = false,
                                                    onClick = {
                                                        navController.navigate(screen.route) {
                                                            popUpTo(navController.graph.findStartDestination().id) {
                                                                saveState = true
                                                            }
                                                            launchSingleTop = true
                                                            restoreState = true
                                                        }

                                                    }
                                                )

                                            }

                                        }
                                    }
                                }


                            }
                        }
                    ) {
                        NavHost(
                            modifier = Modifier.padding(it),
                            navController = navController,
                            startDestination = Graph.AUTHENTICATION
                        ) {
                            authGraph(navController)


                            navigation(route= Graph.HOME, startDestination = Screen.WordScreen.route){

                                composable(route = Screen.SettingsScreen.route) {
                                    SettingsScreen(navController = navController)
                                }
                                composable(route = Screen.WordScreen.route) {
                                    WordScreen(navController = navController)
                                }
                                composable(route = Screen.QuizScreen.route) {
                                    QuizScreen(navController = navController)
                                }
                            }

                            composable(route = Screen.ResultScreen.route) {
                                QuizResultScreen(navController = navController)
                            }
                            composable(route = Screen.QuizHistory.route) {
                                QuizHistoryScreen(navController = navController)
                            }
                        }
                    }


                }
            }
        }



    }
}

fun NavGraphBuilder.authGraph(navController: NavController){
    navigation(route= Graph.AUTHENTICATION, startDestination = Screen.SignInScreen.route){

        composable(route = Screen.SignUpScreen.route) {
            SignUpScreen(navController = navController)
        }
        composable(route = Screen.SignInScreen.route) {
            SignInScreen(navController = navController)
        }
    }
}



object Graph{
    const val ROOT=""
    const val AUTHENTICATION="auth_graph"
    const val HOME="home_graph"

}
