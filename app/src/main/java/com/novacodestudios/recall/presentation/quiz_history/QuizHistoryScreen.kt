package com.novacodestudios.recall.presentation.quiz_history

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.recall.R
import com.novacodestudios.recall.domain.model.QuizDetail
import com.novacodestudios.recall.util.relativeTimeAgo


@Composable
fun QuizHistoryScreen(
    viewModel: QuizHistoryViewModel = hiltViewModel(),
    context: Context,
    onNavigateToQuestionScreen: (activeQuizId: String) -> Unit,
    onNavigateToResultScreen: (quizId: String) -> Unit,
) {
    val state = viewModel.state

    LazyColumn(contentPadding = PaddingValues(8.dp)) {
        state.activeQuiz?.let { activeQuiz ->
            item(key = activeQuiz.id) {
                NewQuizRow(onClick = { onNavigateToQuestionScreen(activeQuiz.id.toString()) })
            }
        }
        item {
            Divider()
        }
        items(state.pastQuizzes, key = { it.id }) { quizDetail ->
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
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailRowItem(
                    title = stringResource(id = R.string.correct) + ": ",
                    value = quizDetail.correctAnswerCount.toString(),
                    titleWeight = FontWeight.Bold
                )
                DetailRowItem(
                    title = stringResource(id = R.string.wrong) + ": ",
                    value = quizDetail.wrongAnswerCount.toString(),
                    titleWeight = FontWeight.Bold
                )
                DetailRowItem(
                    title = stringResource(id = R.string.success_rate) + ": ",
                    value = "%${quizDetail.successRate}",
                    titleWeight = FontWeight.Bold
                )
            }
        },
        secondaryText = { Text(text = relativeTimeAgo(quizDetail.creationDate, context)) }
    )

    /* Column(modifier = Modifier
         .fillMaxWidth()
         .clickable { onClick() },) {
         Row(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(vertical = 8.dp),
             verticalAlignment = Alignment.CenterVertically,
             horizontalArrangement = Arrangement.SpaceEvenly
         ) {
             DetailItem(title = stringResource(id = R.string.correct), value = quizDetail.correctAnswerCount.toString(),titleWeight = FontWeight.Bold)
             DetailItem(title = stringResource(id = R.string.wrong), value = quizDetail.wrongAnswerCount.toString(),titleWeight = FontWeight.Bold)
             DetailItem(title = stringResource(id = R.string.succes_rate), value = "%${quizDetail.successRate}",titleWeight = FontWeight.Bold)

         }
       //  Text(text =relativeTimeAgo(quizDetail.creationDate, context), modifier = Modifier.padding(start = 16.dp))
     }*/

    Divider()
}


@Composable
fun NewQuizRow(onClick: () -> Unit) {
    Card(modifier = Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onClick() }) {
            Column {
                Text(
                    text = stringResource(id = R.string.have_test),
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )
                Text(
                    text = stringResource(id = R.string.click_start),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
            }

        }
    }

}

@Composable
fun DetailRowItem(title: String, titleWeight: FontWeight? = null, value: String) {
    Row {
        Text(text = title, fontWeight = titleWeight)
        Text(text = value)
    }
}






