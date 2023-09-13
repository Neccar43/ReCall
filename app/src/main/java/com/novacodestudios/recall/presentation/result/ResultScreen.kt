package com.novacodestudios.recall.presentation.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.recall.R
import com.novacodestudios.recall.domain.model.Question
import com.novacodestudios.recall.presentation.util.StandardButton
import com.novacodestudios.recall.presentation.util.StandardText


@Composable
fun ResultScreen(
    onNavigateToMainGraph: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                StandardText(
                    text = stringResource(id = R.string.result_screen_title),
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize
                )
                StandardText(
                    text = stringResource(id = R.string.correct_answer_count) + " " + state.correctAnswerCount,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
                StandardText(
                    text = stringResource(id = R.string.wrong_answer_count) + " " + state.wrongAnswerCount,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(state.questions) {
                QuestionItem(question = it)
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
            }
            item {
                StandardButton(
                    onClick = { onNavigateToMainGraph() },
                    text = stringResource(id = R.string.next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun QuestionItem(question: Question) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (question.userAnswer == question.correctAnswer) Color(0xFFC5E1A5)
                else Color(0xFFFFCDD2)
            )
    ) {
        StandardText(
            text = stringResource(id = R.string.question) + " " + question.title,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
        StandardText(
            text = stringResource(id = R.string.your_answer) + " " + question.userAnswer,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
        StandardText(
            text = stringResource(id = R.string.correct_answer) + " " + question.correctAnswer,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
    }
}



