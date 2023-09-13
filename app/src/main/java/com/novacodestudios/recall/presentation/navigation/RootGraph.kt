package com.novacodestudios.recall.presentation.navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseUser
import com.novacodestudios.recall.R
import com.novacodestudios.recall.presentation.question.QuestionScreen
import com.novacodestudios.recall.presentation.result.ResultScreen
import com.novacodestudios.recall.presentation.util.BottomNavigationItem
import com.novacodestudios.recall.presentation.util.Screen
import com.novacodestudios.recall.util.Constants

@Composable
fun RootGraph(navController: NavHostController, currentUser: FirebaseUser?, context: Context) {
    Scaffold(bottomBar = {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.parent?.route
        if (currentRoute == Graph.MAIN) {
            BottomNavBar(navController = navController)
        }
    }) { paddingValue ->
        NavHost(
            modifier = Modifier.padding(paddingValue),
            navController = navController,
            startDestination = if (currentUser == null) Graph.AUTH else Graph.MAIN,
            route = Graph.ROOT
        ) {
            authGraph(navController)
            mainGraph(navController, context)

            composable(
                Screen.QuestionScreen.route + "/{${Constants.QUIZ_ID}}",
                arguments = listOf(navArgument(Constants.QUIZ_ID) { type = NavType.IntType })
            ) {
                QuestionScreen(onNavigateToResultScreen = {
                    navController.navigate(
                        Screen.ResultScreen.route + "/$it"
                    ) {
                        popUpTo(Graph.MAIN) {
                            inclusive = true
                        }
                    }
                })
            }
            composable(
                Screen.ResultScreen.route + "/{${Constants.QUIZ_ID}}",
                arguments = listOf(navArgument(Constants.QUIZ_ID) { type = NavType.IntType })
            ) {
                ResultScreen(onNavigateToMainGraph = {
                    navController.navigate(Graph.MAIN) {
                        popUpTo(Graph.MAIN) {
                            inclusive = true
                        }
                    }
                })
            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(navController: NavController) {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    val items = listOf(
        BottomNavigationItem(
            title = stringResource(id = R.string.words_screen),
            selectedIcon = Icons.Filled.ListAlt,
            unSelectedIcon = Icons.Outlined.ListAlt,
            hasNews = false,
            route = Screen.WordScreen.route
        ),
        BottomNavigationItem(
            title = stringResource(id = R.string.quizzes_screen),
            selectedIcon = Icons.Filled.Quiz,
            unSelectedIcon = Icons.Outlined.Quiz,
            hasNews = false,
            route = Screen.QuizHistory.route
        ),
        BottomNavigationItem(
            title = stringResource(id = R.string.settings_screen),
            selectedIcon = Icons.Filled.Settings,
            unSelectedIcon = Icons.Outlined.Settings,
            hasNews = false,
            route = Screen.SettingsScreen.route
        ),
    )
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                label = { Text(text = item.title) },
                alwaysShowLabel = false,
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    BadgedBox(badge = {
                        if (item.hasNews) {
                            Badge()
                        }
                    }) {
                        Icon(
                            imageVector = if (selectedItemIndex == index) {
                                item.selectedIcon
                            } else {
                                item.unSelectedIcon
                            },
                            contentDescription = item.title
                        )
                    }
                })
        }
    }
}