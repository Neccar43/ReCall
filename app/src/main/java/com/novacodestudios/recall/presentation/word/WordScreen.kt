package com.novacodestudios.recall.presentation.word

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.recall.R
import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.presentation.util.StandardDialog
import com.novacodestudios.recall.presentation.util.StandardSearchBar
import com.novacodestudios.recall.presentation.util.StandardTextField
import com.novacodestudios.recall.util.isNotNull
import java.util.Locale


@Composable
fun WordScreen(viewModel: WordViewModel = hiltViewModel()) {
    val state = viewModel.state
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(WordEvent.OnAddDialogVisibilityChange(true)) },
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_word)
                )
            }
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StandardSearchBar(
                hint = stringResource(id = R.string.search_word),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                onSearch = { viewModel.onEvent(WordEvent.OnSearchChanged(it)) },
                text = state.search
            )

            Spacer(modifier = Modifier.height(16.dp))

            WordList(words = state.wordList, viewModel)
            if (state.isAddDialogVisible) {
                StandardDialog(
                    title = stringResource(id = R.string.add_word),
                    onDismiss = { viewModel.onEvent(WordEvent.OnAddDialogVisibilityChange(false)) },
                    onRequest = { viewModel.onEvent(WordEvent.SaveWord) }
                ) {
                    val dialogModifier = Modifier.padding(horizontal = 8.dp)
                    StandardTextField(
                        hint = stringResource(id = R.string.word),
                        modifier = dialogModifier,
                        onValueChange = { viewModel.onEvent(WordEvent.OnWordChanged(it)) },
                        text = state.word,
                        isError = state.wordError.isNotNull(),
                        supportingText = state.wordError
                    )
                    StandardTextField(
                        hint = stringResource(id = R.string.meaning),
                        modifier = dialogModifier,
                        onValueChange = { viewModel.onEvent(WordEvent.OnMeaningChanged(it)) },
                        text = state.meaning,
                        isError = state.meaningError.isNotNull(),
                        supportingText = state.meaningError
                    )
                }
            }
            if (state.isDeleteDialogVisible) {
                StandardDialog(
                    title = stringResource(id = R.string.delete_word),
                    onDismiss = {
                        viewModel.onEvent(
                            WordEvent.OnDeleteDialogVisibilityChange(
                                false,
                                null
                            )
                        )
                    },
                    onRequest = { viewModel.onEvent(WordEvent.DeleteWord) }
                ) {
                    Text(text = stringResource(id = R.string.are_you_sure_delete_word))
                }
            }
            if (state.isUpdateDialogVisible) {
                StandardDialog(
                    title = stringResource(id = R.string.update_word),
                    onDismiss = { viewModel.onEvent(WordEvent.OnUpdateDialogVisibilityChange(false)) },
                    onRequest = { viewModel.onEvent(WordEvent.UpdateWord) }
                ) {
                    val dialogModifier = Modifier.padding(horizontal = 8.dp)
                    StandardTextField(
                        hint = stringResource(id = R.string.word),
                        modifier = dialogModifier,
                        onValueChange = { viewModel.onEvent(WordEvent.OnWordChanged(it)) },
                        text = state.word,
                        isError = state.wordError.isNotNull(),
                        supportingText = state.wordError
                    )
                    StandardTextField(
                        hint = stringResource(id = R.string.meaning),
                        modifier = dialogModifier,
                        onValueChange = { viewModel.onEvent(WordEvent.OnMeaningChanged(it)) },
                        text = state.meaning,
                        isError = state.meaningError.isNotNull(),
                        supportingText = state.meaningError
                    )
                }
            }
        }
    }
}


@Composable
fun WordList(words: List<Word>, viewModel: WordViewModel) {
    LazyColumn(contentPadding = PaddingValues(8.dp)) {
        item {
            Divider()
        }
        items(words) {
            WordItem(word = it, viewModel = viewModel)
            Divider()
        }
    }
}



@Composable
fun WordItem(
    word: Word,
    viewModel: WordViewModel
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    Box {
        Row(modifier = Modifier.padding(vertical = 9.dp, horizontal = 15.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    fontWeight = FontWeight.Bold
                )
                Text(text = word.meaning.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                })
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (word.repetitions>3){
                    Card {
                        Row(modifier = Modifier.padding(horizontal = 3.dp)) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = stringResource(id = R.string.streak),
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = word.repetitions.toString())
                        }
                    }
                }


                IconButton(
                    onClick = { isExpanded = true },
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(id = R.string.more),
                    )
                }
            }

        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            offset = DpOffset(x = (-9).dp, y = (0).dp)
        ) {
            DropdownMenuItem(onClick = {
                viewModel.onEvent(WordEvent.OnUpdateDialogVisibilityChange(true))
                viewModel.onEvent(WordEvent.OnSetUpdatedWord(word))
                isExpanded = false
            }) {
                Text(text = stringResource(id = R.string.update_word))
            }
            DropdownMenuItem(onClick = {
                viewModel.onEvent(
                    WordEvent.OnDeleteDialogVisibilityChange(
                        true,
                        deletedWord = word
                    )
                )
                isExpanded = false
            }) {
                Text(text = stringResource(id = R.string.delete_word))
            }
        }

    }

}









