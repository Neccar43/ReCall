package com.novacodestudios.recall.presentation.quiz_history

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.novacodestudios.recall.presentation.util.Screen
import com.novacodestudios.recall.util.StandardText
import com.novacodestudios.recall.ui.theme.ReCallTheme

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
            items(listOf<Quiz>()) {
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
        StandardText(text = "Doğru sayısı")
        StandardText(text = "Yanlış sayısı")
    }


}




