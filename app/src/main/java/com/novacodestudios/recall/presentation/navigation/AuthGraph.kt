package com.novacodestudios.recall.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.novacodestudios.recall.presentation.sign_in.SignInScreen
import com.novacodestudios.recall.presentation.sign_up.SignUpScreen
import com.novacodestudios.recall.presentation.util.Screen

fun NavGraphBuilder.authGraph(navController: NavController,isSplash:Boolean) {
    navigation(startDestination = Screen.SignInScreen.route, route = Graph.AUTH) {
        composable(
            route = Screen.SignUpScreen.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            },
        ) {
            SignUpScreen(
                onNavigateToSignInScreen = {
                    navController.navigate(Screen.SignInScreen.route) {
                        popUpTo(Screen.SignInScreen.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToHomeGraph = {
                    navController.navigate(Graph.MAIN) {
                        popUpTo(Graph.AUTH) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            route = Screen.SignInScreen.route,
            enterTransition = {
                if (isSplash) {
                    fadeIn()
                } else {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                }

            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)

                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)

                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)

                )
            }) {
            SignInScreen(
                onNavigateToSignUpScreen = {
                    navController.navigate(Screen.SignUpScreen.route) {
                        popUpTo(Screen.SignUpScreen.route)
                    }
                },
                onNavigateToMainGraph = {
                    navController.navigate(Graph.MAIN) {
                        popUpTo(Graph.AUTH) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        // TODO: Şifremi unuttum ekranlarını da ekle
    }
}
