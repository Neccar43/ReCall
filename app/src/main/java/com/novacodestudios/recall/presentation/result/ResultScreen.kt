package com.novacodestudios.recall.presentation.result

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.recall.R
import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.presentation.util.StandardButton
import com.novacodestudios.recall.util.formatTime
import kotlinx.coroutines.delay


@Composable
fun ResultScreen(
    onNavigateToMainGraph: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val quizDetail = viewModel.state.quizDetail ?: return
    val quizWithQuestions = viewModel.state.quizWithQuestions ?: return
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HalfCircleBackground()
        RippleLoadingAnimation(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 85.dp)
        )
        SuccessCircle(
            successRate = quizDetail.successRate,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 200.dp)
        )
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 350.dp)
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DetailItem(title = stringResource(id = R.string.correct), value = quizDetail.correctAnswerCount.toString())
                DetailItem(title = stringResource(id = R.string.wrong), value = quizDetail.wrongAnswerCount.toString())
                DetailItem(
                    title = stringResource(id = R.string.duration),
                    value = formatTime(quizDetail.totalCompletionTime, context)
                )
            }
        }
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp)
                .padding(top = 470.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                quizWithQuestions.questions.forEach { question ->
                    QuestionItem(question = question)
                    Divider()
                }
                StandardButton(
                    onClick = { onNavigateToMainGraph() },
                    text = stringResource(id = R.string.next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)

                )

            }


        }
    }

}

@Composable
fun QuestionItem(question: Question) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val isCorrect = question.userAnswer == question.correctAnswer
        val wrongColor = MaterialTheme.colorScheme.error
        val correctColor = MaterialTheme.colorScheme.primary
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text =stringResource(id = R.string.word))
            Text(text =question.title)
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text =stringResource(id = R.string.meaning))
            Text(text =question.correctAnswer)
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text =stringResource(id = R.string.your_answer))
            Text(
                text = question.userAnswer.toString(),
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                color = if (isCorrect) correctColor else wrongColor
            )
        }

    }
}

@Composable
fun DetailItem(title: String, value: String,titleWeight:FontWeight?=null) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, fontWeight = titleWeight)
        Text(text = value)
    }
}


@Composable
fun SuccessCircle(successRate: String, modifier: Modifier = Modifier) {
    val onBackGround = MaterialTheme.colorScheme.onPrimary

    val radius1 by animateFloatAsState(targetValue = 200f, label = "")
    Box(
        modifier = modifier
            .wrapContentSize()
            .drawBehind {
                drawCircle(
                    color = onBackGround,
                    center = center,
                    radius = radius1
                )
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(id = R.string.success_rate), color = Color.Black)
            Text(
                text = "%$successRate",
                color = Color.Black,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
            Spacer(
                modifier = Modifier
            )
        }

    }
}

@Composable
fun RippleLoadingAnimation(
    modifier: Modifier = Modifier,
    circleColor: Color = MaterialTheme.colorScheme.onPrimary,
    animationDelay: Int = 2000,
) {
    val circles = listOf(
        remember {
            Animatable(initialValue = 0f)
        },
        remember {
            Animatable(initialValue = 0f)
        },
        remember {
            Animatable(initialValue = 0f)
        },
        remember {
            Animatable(initialValue = 0f)
        }

    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(Unit) {
            delay(timeMillis = (animationDelay / 4L) * (index + 1))

            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDelay,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Box(
        modifier = modifier
            .size(size = 300.dp)
            .background(color = Color.Transparent)
    ) {
        circles.forEachIndexed { _, animatable ->
            Box(
                modifier = Modifier
                    .scale(scale = animatable.value)
                    .size(size = 300.dp)
                    .clip(shape = CircleShape)
                    .background(
                        color = circleColor
                            .copy(alpha = (1 - animatable.value))
                    )
            ) {
            }
        }
    }
}

@Composable
fun HalfCircleBackground() {
    val primaryColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f)
            .drawBehind {
                drawCircle(
                    color = primaryColor,
                    radius = 1100f,
                    center = center
                )
            }
            .background(primaryColor)
    )
}

