package com.novacodestudios.recall.view

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.novacodestudios.recall.Screen
import com.novacodestudios.recall.util.StandardText
import com.novacodestudios.recall.ui.theme.ReCallTheme
import com.novacodestudios.recall.viewmodel.QuizHistoryViewModel

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewDarkScreen() {
    ReCallTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            QuizHistoryScreen(navController = rememberNavController())
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun QuizHistoryScreen(
    navController: NavController,
    viewModel: QuizHistoryViewModel = hiltViewModel()
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.QuizScreen.route) {
                        //popUp yap
                    }
                },
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Default.PostAdd,
                    contentDescription = "Create"
                )
            }
        }
    ) {
        LazyColumn(modifier = Modifier) {
            items(quizList) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    QuizRow(quiz = it)
                }
            }
        }
    }

}

@Composable
fun QuizRow(quiz: Quiz, onClick: () -> Unit = {}) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        StandardText(text = quiz.date)
        StandardText(text = quiz.trueCount.toString())
        StandardText(text = quiz.falseCount.toString())
        StandardText(text = "%" + (quiz.successRate * 100).toString())
    }


}


val quizList = listOf(
    Quiz(10, 5, 0.66, "2023-06-01"),
    Quiz(8, 2, 0.8, "2023-06-02"),
    Quiz(15, 3, 0.83, "2023-06-03"),
    Quiz(12, 4, 0.75, "2023-06-04"),
    Quiz(9, 1, 0.9, "2023-06-05"),
    Quiz(11, 3, 0.79, "2023-06-06"),
    Quiz(13, 2, 0.87, "2023-06-07"),
    Quiz(7, 4, 0.64, "2023-06-08"),
    Quiz(16, 1, 0.94, "2023-06-09"),
    Quiz(10, 2, 0.83, "2023-06-10"),
    Quiz(14, 3, 0.82, "2023-06-11"),
    Quiz(11, 5, 0.69, "2023-06-12"),
    Quiz(9, 3, 0.75, "2023-06-13"),
    Quiz(12, 2, 0.86, "2023-06-14"),
    Quiz(8, 1, 0.89, "2023-06-15"),
    Quiz(13, 4, 0.76, "2023-06-16"),
    Quiz(10, 3, 0.77, "2023-06-17"),
    Quiz(7, 2, 0.78, "2023-06-18"),
    Quiz(15, 1, 0.93, "2023-06-19"),
    Quiz(11, 4, 0.73, "2023-06-20")
)

data class Quiz(val trueCount: Int, val falseCount: Int, val successRate: Double, val date: String)