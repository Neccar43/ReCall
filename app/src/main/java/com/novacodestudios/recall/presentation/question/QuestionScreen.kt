package com.novacodestudios.recall.presentation.question

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.recall.R
import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.presentation.util.StandardButton
import com.novacodestudios.recall.presentation.util.StandardCircularIndicator
import com.novacodestudios.recall.presentation.util.StandardDialog
import com.novacodestudios.recall.presentation.util.StandardText
import com.novacodestudios.recall.presentation.util.StandardTextField
import kotlinx.coroutines.flow.collectLatest

@Composable
fun QuestionScreen(
    viewModel: QuestionViewModel = hiltViewModel(),
    onNavigateToResultScreen: (quizId: String) -> Unit,
    onNavigateToMainGraph: () -> Unit

) {

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is QuestionViewModel.UIEvent.FinishQuiz -> onNavigateToResultScreen(event.quizId.toString())
            }
        }
    }
    /*val primaryColor = MaterialTheme.colorScheme.primary


    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(fraction = 0.5f)
        .clip(RoundedCornerShape(bottomStartPercent = 5, bottomEndPercent =5 ))
        .background(primaryColor)
    )*/
    val state = viewModel.state
    var isCancelClicked by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        var isVisible by remember { mutableStateOf(true) }
        if (isVisible) {
            StandardButton(
                onClick = {
                    isVisible = !isVisible
                    viewModel.onEvent(QuestionEvent.StartTime)
                },
                text = stringResource(id = R.string.start_quiz),
                modifier = Modifier
                    .align(Alignment.Center)
            )
        } else {
            val progress by animateFloatAsState(
                (state.questionIndex + 1) / state.questions.size.toFloat(),
                label = ""
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                OutlinedButton(
                    onClick = { isCancelClicked = true },
                ) {
                    Text(text = stringResource(id = R.string.skip))
                }
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            QuestionCard(
                modifier = Modifier.align(Alignment.Center),
                question = state.questions[state.questionIndex],
                text = state.answer,
                onAnswerChange = { viewModel.onEvent(QuestionEvent.AnswerChange(it)) },
                onSubmitClick = {
                    val responseTime = System.currentTimeMillis() - state.startTime
                    viewModel.onEvent(QuestionEvent.SubmitAnswer(state.answer, responseTime))

                    if (state.questionIndex != state.questions.size - 1) {
                        viewModel.onEvent(QuestionEvent.StartTime)
                    } else {
                        viewModel.onEvent(QuestionEvent.FinishQuiz)
                    }

                }
            )
        }
        StandardCircularIndicator(isLoading = state.isLoading)

        if (isCancelClicked) {
            StandardDialog(
                title = stringResource(id = R.string.skip_quiz),
                onDismiss = { isCancelClicked = false },
                onRequest = { onNavigateToMainGraph() }
            ) {
                StandardText(text = stringResource(id = R.string.are_you_sure_skip_quiz))
            }
        }

    }
}

@Composable
fun QuestionCard(
    question: Question,
    modifier: Modifier = Modifier,
    text: String,
    onAnswerChange: (String) -> Unit,
    onSubmitClick: () -> Unit
) {
    Card(
        modifier = modifier.padding(40.dp)

    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StandardText(
                text = stringResource(id = R.string.translate_word),
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                modifier = Modifier
                    .padding(8.dp)
            )
            StandardText(
                text = question.title,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                modifier = Modifier
                    .padding(8.dp)
            )
            StandardTextField(
                hint = stringResource(id = R.string.write_translation),
                text = text,
                onValueChange = { onAnswerChange(it) },
                modifier = Modifier
            )
            StandardButton(
                onClick = { onSubmitClick() },
                text = stringResource(id = R.string.submit),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }

    }
}