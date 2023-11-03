package com.novacodestudios.recall.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import com.novacodestudios.recall.R
import kotlinx.coroutines.delay

@Composable
    fun SplashScreen(onNavigateToMainGraphOrAuthGraph: () -> Unit) {
        val scale = remember { Animatable(1f) }
        LaunchedEffect(true) {
            delay(1000L)
            scale.animateTo(
                targetValue = 40.0f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
            onNavigateToMainGraphOrAuthGraph()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF8238BD)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.foreground),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    },
                tint = Color.White
            )
        }
    }