package com.novacodestudios.recall.presentation.quiz_history

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.recall.domain.model.Quiz
import com.novacodestudios.recall.domain.model.QuizDetail
import com.novacodestudios.recall.util.formatTime
import com.novacodestudios.recall.util.relativeTimeAgo


@Composable
fun QuizHistoryScreen(
    viewModel: QuizHistoryViewModel = hiltViewModel(),
    context: Context,
    onNavigateToQuestionScreen:(activeQuizId:String)->Unit,
    onNavigateToResultScreen:(quizId:String)->Unit,
) {
    val state = viewModel.state

    LazyColumn(contentPadding = PaddingValues(8.dp)) {
        state.activeQuiz?.let { activeQuiz ->
            item(key = activeQuiz.id) {
                NewQuizRow(activeQuiz,
                    onClick = { onNavigateToQuestionScreen(activeQuiz.id.toString()) })
            }
        }
        item {
            Divider()
        }
        items(state.pastQuizzes,key = {it.id}) {quizDetail->
            QuizItem(
                quizDetail = quizDetail,
                context = context,
                onClick = { onNavigateToResultScreen(quizDetail.id.toString()) })
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QuizItem(quizDetail: QuizDetail, context: Context, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        text = { Text(
            text = "Doğru:${quizDetail.correctAnswerCount}   Yanlış:${quizDetail.wrongAnswerCount}   Başarı oranı: %${quizDetail.successRate}",
            fontWeight = FontWeight.Bold,

        ) },
        secondaryText = { Text(text = relativeTimeAgo(quizDetail.creationDate, context) + "   Geçen süre:${formatTime(quizDetail.totalCompletionTime)}") }
    )
    Divider()
}


@Composable
fun NewQuizRow(quiz: Quiz, onClick: () -> Unit) {
    Card(modifier = Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onClick() }) {
            Column {
                Text(
                    text = "Hazırda testiniz var.",
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )
                Text(
                    text = "Başlamak için tıklayın",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
            }

        }
    }

}






