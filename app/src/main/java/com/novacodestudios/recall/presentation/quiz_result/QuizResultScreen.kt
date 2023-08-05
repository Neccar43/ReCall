package com.novacodestudios.recall.presentation.quiz_result

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.novacodestudios.recall.util.StandardButton
import com.novacodestudios.recall.util.StandardText
import com.novacodestudios.recall.ui.theme.ReCallTheme

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewScreen() {
    ReCallTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController= rememberNavController()
            QuizResultScreen(navController)
        }
    }
}

@Composable
fun QuizResultScreen(navController: NavController, viewModel: QuizResultViewModel = hiltViewModel()) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(modifier = Modifier) {
            ResultContent(5,5,50.0)

        }
    }
}

@Composable
fun ResultContent(trueCount:Int,falseCount:Int,success:Double) {
    Column(Modifier.padding(8.dp)) {
        StandardText(
            text = "Sınav tamamlandı",
            fontSize = MaterialTheme.typography.headlineLarge.fontSize
        )
        Spacer(modifier = Modifier.height(20.dp))
        StandardText(text = "Doğru:  $trueCount")
        Spacer(modifier = Modifier.height(10.dp))
        StandardText(text = "Yanlış:  $falseCount")
        Spacer(modifier = Modifier.height(10.dp))
        StandardText(text = "Başarı yüzdesi:  %$success")
        Spacer(modifier = Modifier.height(20.dp))
        StandardButton(
            onClick = { /*TODO*/ },
            text = "Tamam",
            modifier = Modifier
                .align(CenterHorizontally)
                .fillMaxWidth(0.7f)

        )
    }
}