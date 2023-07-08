package com.novacodestudios.recall.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.novacodestudios.recall.roomdb.table.Word
import com.novacodestudios.recall.util.StandardDialog
import com.novacodestudios.recall.util.StandardSearchBar
import com.novacodestudios.recall.util.StandardTextField
import com.novacodestudios.recall.ui.theme.ReCallTheme
import com.novacodestudios.recall.viewmodel.WordViewModel


@Preview(showSystemUi = true)
@Composable
fun PreviewLightScreen() {
    ReCallTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            WordScreen(navController = rememberNavController())
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WordScreen(navController: NavController, viewModel: WordViewModel = hiltViewModel()) {
    var visible by remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    visible = !visible
                },
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create"
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StandardSearchBar(
                hint = "Kelime arayın",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            WordTable()
            if (visible) {
                StandardDialog(
                    title = "Kelime ekleyin",
                    onDismiss = { visible = false },
                    onRequest = {}
                ) {
                    val dialogModifier = Modifier.padding(horizontal = 8.dp)
                    StandardTextField(hint = "Kelime", modifier = dialogModifier)
                    StandardTextField(hint = "Anlamı", modifier = dialogModifier)
                }
            }


        }
    }


}


@Composable
fun WordTable() {
    LazyColumn(modifier = Modifier) {
        items(wordList) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                WordRow(word = it)
            }
        }
    }
}

@Composable
fun WordRow(word: Word, fontSize: TextUnit = TextUnit.Unspecified) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = word.originalName,
            fontSize = fontSize,
            modifier = Modifier.weight(0.4f),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = word.translations,
            fontSize = fontSize,
            modifier = Modifier.weight(0.4f),
            fontWeight = FontWeight.Bold
        )


    }


}





