package com.novacodestudios.recall.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.novacodestudios.recall.Screen
import com.novacodestudios.recall.util.StandardButton
import com.novacodestudios.recall.util.StandardText
import com.novacodestudios.recall.util.StandardTextField
import com.novacodestudios.recall.ui.theme.ReCallTheme
import com.novacodestudios.recall.viewmodel.QuizHistoryViewModel
import com.novacodestudios.recall.viewmodel.QuizViewModel

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewQuizScreen() {
    ReCallTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            QuizScreen(navController = rememberNavController())
        }
    }
}

@Composable
fun QuizScreen(navController: NavController,viewModel: QuizViewModel = hiltViewModel()) {
    var index by remember {
        mutableStateOf(0)
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedIconButton(
                onClick = {
                    navController.navigate(Screen.QuizHistory.route) {
                    //popUp yapılacak
                    }
                }
            ) {
                Icon(imageVector = Icons.Outlined.Close, contentDescription = "Çıkış")
            }
            LinearProgressIndicator(
                progress = (index + 1) / wordList.size.toFloat(),
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        QuestionCard(
            question = wordList[index].name,
            answer = wordList[index].wordTranslation[0].translation,
            skipNextQuestion = { isCorrect ->
                if (isCorrect)
                    index++
            })

    }
}

@Composable
fun QuestionCard(
    question: String,
    answer: String,
    skipNextQuestion: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember {
        mutableStateOf("")
    }
    Card(modifier = modifier.wrapContentSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            StandardText(text = question, modifier = Modifier.padding(8.dp))
            StandardTextField(
                hint = "Cevap",
                getText = {
                    text = it
                },
                modifier = Modifier.padding(8.dp)
            )
            StandardButton(
                onClick = {
                    skipNextQuestion(answer == text)
                },
                text = "Onayla",
                enabled = text.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
            )
        }


    }
}





