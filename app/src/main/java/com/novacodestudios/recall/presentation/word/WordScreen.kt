package com.novacodestudios.recall.presentation.word

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.recall.R
import com.novacodestudios.recall.domain.model.Word
import com.novacodestudios.recall.domain.util.OrderType
import com.novacodestudios.recall.domain.util.WordOrder
import com.novacodestudios.recall.presentation.util.EmptyStateMessage
import com.novacodestudios.recall.presentation.util.StandardCircularIndicator
import com.novacodestudios.recall.presentation.util.StandardDialog
import com.novacodestudios.recall.presentation.util.StandardSearchBar
import com.novacodestudios.recall.presentation.util.StandardTextField
import com.novacodestudios.recall.presentation.util.UIText
import com.novacodestudios.recall.util.isNotNull
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordScreen(viewModel: WordViewModel = hiltViewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { text ->
            when (text) {
                is UIText.DynamicText -> snackbarHostState.showSnackbar(text.value)
                is UIText.StringResource -> snackbarHostState.showSnackbar(text.asString(context = context))
            }

        }
    }
    val state = viewModel.state
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
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
        },
        topBar = {
            var active by remember {
                mutableStateOf(false)
            }
            val isLongClicked = state.isLongClicked
            TopAppBar(
                title = {
                    AnimatedContent(targetState = isLongClicked,
                        label = "",
                        transitionSpec = {
                            slideInVertically(
                                initialOffsetY = {
                                    if (isLongClicked) it else -it
                                }
                            ) togetherWith slideOutVertically(
                                targetOffsetY = {
                                    if (isLongClicked) -it else it
                                }
                            )
                        }) { isLongClicked ->
                        if (isLongClicked) {
                            Row {
                                Checkbox(
                                    checked = state.wordList.size == state.selectedWords.size,
                                    onCheckedChange = {
                                        if (state.wordList.size == state.selectedWords.size) {
                                            viewModel.onEvent(WordEvent.OnAllWordsUnSelected)
                                        } else {
                                            viewModel.onEvent(WordEvent.OnAllWordsSelected)
                                        }
                                    })
                                Text(
                                    text = "${state.selectedWords.size} " + stringResource(id = R.string.selected),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                            BackHandler {
                                viewModel.onEvent(WordEvent.OnLongClickedChange)
                            }
                        } else {
                            Box(contentAlignment = Alignment.Center) {
                                val width by animateDpAsState(
                                    targetValue = if (active) 300.dp else 48.dp,
                                    label = "",
                                    animationSpec = tween(easing = LinearOutSlowInEasing),
                                )
                                Box(
                                    contentAlignment = Alignment.CenterEnd,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    StandardSearchBar(
                                        hint = stringResource(id = R.string.search_word),
                                        modifier = Modifier
                                            .height(if (active) 63.dp else 56.dp)
                                            .width(width),
                                        onSearch = { viewModel.onEvent(WordEvent.OnSearchChanged(it)) },
                                        text = state.search,
                                        cornerRadius = 56,
                                        active = active,
                                        onActiveChange = { active = it }
                                    )
                                }

                                AnimatedVisibility(
                                    visible = !active,
                                    modifier = Modifier.align(Alignment.CenterStart),
                                    enter = slideInHorizontally(),
                                    exit = slideOutHorizontally { -it }
                                ) {
                                    var isGroupMenuExpanded by remember {
                                        mutableStateOf(false)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            isGroupMenuExpanded = !isGroupMenuExpanded
                                        }) {
                                        Text(
                                            text = state.selectedGroup?.groupName
                                                ?: stringResource(id = R.string.all_words),
                                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ExpandMore,
                                            contentDescription = stringResource(id = R.string.select_group)
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = isGroupMenuExpanded,
                                        onDismissRequest = { isGroupMenuExpanded = false },
                                    ) {
                                        if (state.groups.isNotEmpty()) {
                                            state.groups.forEach { group ->
                                                DropdownMenuItem(
                                                    onClick = {
                                                        viewModel.onEvent(
                                                            WordEvent.OnSelectedGroupChanged(
                                                                group
                                                            )
                                                        )
                                                        isGroupMenuExpanded = false
                                                    },
                                                    modifier = Modifier
                                                ) {
                                                    Box {
                                                        var isGroupEditMenuExpanded by remember {
                                                            mutableStateOf(false)
                                                        }

                                                        Row(
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            Text(
                                                                text = group.groupName,
                                                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                                                            )
                                                            IconButton(
                                                                onClick = {
                                                                    isGroupEditMenuExpanded =
                                                                        !isGroupEditMenuExpanded
                                                                },
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.MoreVert,
                                                                    contentDescription = stringResource(
                                                                        id = R.string.update_delete_group
                                                                    )
                                                                )
                                                            }
                                                        }

                                                        DropdownMenu(
                                                            expanded = isGroupEditMenuExpanded,
                                                            onDismissRequest = {
                                                                isGroupEditMenuExpanded = false
                                                            },
                                                            offset =
                                                            DpOffset(
                                                                x = (170).dp,
                                                                y = (16).dp
                                                            )


                                                        ) {
                                                            DropdownMenuItem(
                                                                onClick = {
                                                                    viewModel.onEvent(WordEvent.OnUpdatedGroupDialogVisibilityChanged)
                                                                    viewModel.onEvent(
                                                                        WordEvent.OnEditedGroupChanged(
                                                                            group
                                                                        )
                                                                    )
                                                                    isGroupEditMenuExpanded = false
                                                                    isGroupMenuExpanded = false
                                                                }
                                                            ) {
                                                                Text(
                                                                    text = stringResource(id = R.string.edit_group),
                                                                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                                                                )
                                                            }
                                                            DropdownMenuItem(
                                                                onClick = {
                                                                    viewModel.onEvent(WordEvent.OnDeleteGroupDialogVisibilityChanged)
                                                                    viewModel.onEvent(
                                                                        WordEvent.OnEditedGroupChanged(
                                                                            group
                                                                        )
                                                                    )
                                                                    isGroupEditMenuExpanded = false
                                                                    isGroupMenuExpanded = false
                                                                }
                                                            ) {
                                                                Text(
                                                                    text = stringResource(id = R.string.delete_group),
                                                                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            DropdownMenuItem(
                                                onClick = {
                                                    viewModel.onEvent(
                                                        WordEvent.OnSelectedGroupChanged(
                                                            null
                                                        )
                                                    )
                                                    isGroupMenuExpanded = false
                                                }
                                            ) {
                                                Text(
                                                    text = stringResource(id = R.string.all_words),
                                                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                                                )
                                            }
                                        } else
                                            DropdownMenuItem(onClick = { }) {
                                                Text(
                                                    text = stringResource(id = R.string.no_group),
                                                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                                                )

                                            }

                                    }


                                }


                            }
                        }
                    }
                },
                actions = {
                    AnimatedContent(targetState = isLongClicked,
                        label = "",
                        transitionSpec = {
                            slideInVertically(
                                initialOffsetY = {
                                    if (isLongClicked) it else -it
                                }
                            ) togetherWith slideOutVertically(
                                targetOffsetY = {
                                    if (isLongClicked) -it else it
                                }
                            )
                        }) {
                        if (!it) {
                            Row {
                                IconButton(
                                    onClick = { viewModel.onEvent(WordEvent.OnSheetVisibilityChange) },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sort,
                                        contentDescription = stringResource(id = R.string.sort_words),
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.onEvent(WordEvent.OnGroupDialogVisibilityChanged) },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LibraryAdd,
                                        contentDescription = "Add group",
                                    )
                                }
                            }
                        } else {
                            if (state.selectedWords.isNotEmpty()) {
                                Row {
                                    IconButton(
                                        onClick = { viewModel.onEvent(WordEvent.OnBulkDeleteDialogVisibilityChanged) },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete selected words",
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.onEvent(WordEvent.OnWordsMoveDialogVisibilityChanged) },
                                    ) {
                                        Icon(
                                            painter =
                                            painterResource(
                                                id = R.drawable.move_group_f_ll0_wght400_grad0_opsz24
                                            ),
                                            contentDescription = "Move group selected words"
                                        )
                                    }
                                }
                            }


                        }
                    }


                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValue ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.wordList.isEmpty()) {
                EmptyStateMessage(modifier = Modifier.align(Alignment.Center), messageId = R.string.empty_word_screen)

            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValue),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                            supportingText = state.wordError?.asString()
                        )
                        StandardTextField(
                            hint = stringResource(id = R.string.meaning),
                            modifier = dialogModifier,
                            onValueChange = { viewModel.onEvent(WordEvent.OnMeaningChanged(it)) },
                            text = state.meaning,
                            isError = state.meaningError.isNotNull(),
                            supportingText = state.meaningError?.asString()
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
                        onDismiss = {
                            viewModel.onEvent(
                                WordEvent.OnUpdateDialogVisibilityChange(
                                    false
                                )
                            )
                        },
                        onRequest = { viewModel.onEvent(WordEvent.UpdateWord) }
                    ) {
                        val dialogModifier = Modifier.padding(horizontal = 8.dp)
                        StandardTextField(
                            hint = stringResource(id = R.string.word),
                            modifier = dialogModifier,
                            onValueChange = { viewModel.onEvent(WordEvent.OnWordChanged(it)) },
                            text = state.word,
                            isError = state.wordError.isNotNull(),
                            supportingText = state.wordError?.asString()
                        )
                        StandardTextField(
                            hint = stringResource(id = R.string.meaning),
                            modifier = dialogModifier,
                            onValueChange = { viewModel.onEvent(WordEvent.OnMeaningChanged(it)) },
                            text = state.meaning,
                            isError = state.meaningError.isNotNull(),
                            supportingText = state.meaningError?.asString()
                        )
                    }
                }
                StandardCircularIndicator(isLoading = state.isLoading)

                val sheetState = rememberModalBottomSheetState()

                if (state.isSheetOpen) {
                    ModalBottomSheet(
                        onDismissRequest = { viewModel.onEvent(WordEvent.OnSheetVisibilityChange) },
                        sheetState = sheetState,
                    ) {
                        SheetRow(
                            text = stringResource(id = R.string.order_by_creation_date_ascending),
                            onClick = {
                                viewModel.onEvent(
                                    WordEvent.OnOrderChanged(
                                        WordOrder.CreationDate(
                                            OrderType.Ascending
                                        )
                                    )
                                )
                            },
                            isSelected = state.wordOrder is WordOrder.CreationDate && state.wordOrder.orderType == OrderType.Ascending
                        )

                        SheetRow(
                            text = stringResource(id = R.string.order_by_creation_date_descending),
                            onClick = {
                                viewModel.onEvent(
                                    WordEvent.OnOrderChanged(
                                        WordOrder.CreationDate(
                                            OrderType.Descending
                                        )
                                    )
                                )
                            },
                            isSelected = state.wordOrder is WordOrder.CreationDate && state.wordOrder.orderType == OrderType.Descending
                        )

                        SheetRow(
                            text = stringResource(id = R.string.order_alphabetically_ascending),
                            onClick = {
                                viewModel.onEvent(
                                    WordEvent.OnOrderChanged(
                                        WordOrder.Alphabetically(
                                            OrderType.Ascending
                                        )
                                    )
                                )
                            },
                            isSelected = state.wordOrder is WordOrder.Alphabetically && state.wordOrder.orderType == OrderType.Ascending
                        )

                        SheetRow(
                            text = stringResource(id = R.string.order_alphabetically_descending),
                            onClick = {
                                viewModel.onEvent(
                                    WordEvent.OnOrderChanged(
                                        WordOrder.Alphabetically(
                                            OrderType.Descending
                                        )
                                    )
                                )
                            },
                            isSelected = state.wordOrder is WordOrder.Alphabetically && state.wordOrder.orderType == OrderType.Descending
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                    }

                }

                if (state.isGroupDialogVisible) {
                    StandardDialog(
                        title = stringResource(id = R.string.add_group),
                        onDismiss = { viewModel.onEvent(WordEvent.OnGroupDialogVisibilityChanged) },
                        onRequest = { viewModel.onEvent(WordEvent.OnGroupAdded) }) {
                        StandardTextField(
                            hint = stringResource(id = R.string.group_name),
                            modifier = Modifier,
                            onValueChange = { viewModel.onEvent(WordEvent.OnGroupNameChanged(it)) },
                            text = state.groupName,
                            isError = state.groupNameError.isNotNull(),
                            supportingText = state.groupNameError?.asString()
                        )

                    }
                }

                if (state.isBulkDeleteDialogVisible) {
                    StandardDialog(
                        title = stringResource(id = R.string.bulk_delete),
                        onDismiss = {
                            viewModel.onEvent(
                                WordEvent.OnBulkDeleteDialogVisibilityChanged
                            )
                        },
                        onRequest = { viewModel.onEvent(WordEvent.OnBulkDelete) }
                    ) {
                        Text(text = stringResource(id = R.string.are_you_sure_delete_words))
                    }
                }

                if (state.isWordsMoveDialogVisible) {
                    StandardDialog(
                        title = stringResource(id = R.string.move_group_selected_words),
                        onDismiss = { viewModel.onEvent(WordEvent.OnWordsMoveDialogVisibilityChanged) },
                        onRequest = { viewModel.onEvent(WordEvent.OnWordsMove) }
                    ) {
                        var isExpanded by remember {
                            mutableStateOf(false)
                        }
                        ExposedDropdownMenuBox(
                            expanded = isExpanded,
                            onExpandedChange = { isExpanded = it }) {
                            StandardTextField(
                                text = viewModel.state.groupToMove?.groupName
                                    ?: stringResource(id = R.string.select_group),
                                onValueChange = {},
                                readOnly = true,
                                iconEnd = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = isExpanded,
                                onDismissRequest = {
                                    viewModel.onEvent(
                                        WordEvent.OnGroupToMoveChange(
                                            null
                                        )
                                    );isExpanded = false
                                }) {


                                val menuGroups = state.groups.toMutableList()
                                menuGroups.remove(state.selectedGroup)

                                menuGroups.forEach { group ->
                                    DropdownMenuItem(
                                        onClick = {
                                            viewModel.onEvent(
                                                WordEvent.OnGroupToMoveChange(
                                                    group
                                                )
                                            );isExpanded = false
                                        }
                                    ) {
                                        Text(text = group.groupName)
                                    }
                                }
                            }
                        }
                    }
                }

                if (state.isUpdateGroupDialogVisible) {
                    StandardDialog(
                        title = stringResource(id = R.string.edit_group),
                        onDismiss = { viewModel.onEvent(WordEvent.OnUpdatedGroupDialogVisibilityChanged) },
                        onRequest = { viewModel.onEvent(WordEvent.OnGroupUpdated) }) {
                        StandardTextField(
                            text = state.newGroupName,
                            onValueChange = { viewModel.onEvent(WordEvent.OnNewGroupNameChanged(it)) },
                            hint = stringResource(id = R.string.new_group_name),
                            isError = state.newGroupNameError.isNotNull(),
                            supportingText = state.newGroupNameError?.asString()
                        )

                    }
                }

                if (state.isDeleteGroupDialogVisible) {
                    StandardDialog(
                        title = stringResource(id = R.string.delete_group),
                        onDismiss = { viewModel.onEvent(WordEvent.OnDeleteGroupDialogVisibilityChanged) },
                        onRequest = {
                            viewModel.onEvent(WordEvent.OnGroupDeleted)

                        }
                    ) {
                        Text(text = stringResource(id = R.string.are_you_sure_delete_group))
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = state.isDeleteAllWordsInGroupSelected,
                                onCheckedChange = { viewModel.onEvent(WordEvent.OnDeleteAllWordsCheckChanged) })
                            Text(text = stringResource(id = R.string.delete_all_words_in_group))
                        }

                    }
                }


            }
        }

    }
}




@Composable
fun SheetRow(text: String, onClick: () -> Unit, isSelected: Boolean) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        RadioButton(selected = isSelected, onClick = { onClick() })
        Text(text = text)

    }
}


@Composable
fun WordList(words: List<Word>, viewModel: WordViewModel) {
    LazyColumn(contentPadding = PaddingValues(8.dp)) {
        item {
            Divider()
        }
        items(items = words, key = { it.id }) {
            WordItem(word = it, viewModel = viewModel)
            Divider()
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordItem(
    word: Word,
    viewModel: WordViewModel
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var isSelected by remember {
        mutableStateOf(viewModel.state.selectedWords.contains(word))
    }
    var isMeaningShowing by remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier
        .combinedClickable(
            onLongClick = {
                if (!viewModel.state.isLongClicked) {
                    isSelected = true
                    viewModel.onEvent(WordEvent.OnWordSelected(word))
                }

            },
            onClick = {
                if (viewModel.state.isLongClicked) {
                    if (isSelected) {
                        isSelected = false
                        viewModel.onEvent(WordEvent.OnWordUnSelected(word))
                    } else {
                        isSelected = true
                        viewModel.onEvent(WordEvent.OnWordSelected(word))
                    }
                    return@combinedClickable
                }
                if (!viewModel.state.isMeaningVisible) {
                    isMeaningShowing = !isMeaningShowing

                }

            }
        )
        .background(if (viewModel.state.selectedWords.contains(word)) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 9.dp, horizontal = 15.dp)

        ) {
            if (viewModel.state.isLongClicked) {
                Checkbox(
                    checked = viewModel.state.selectedWords.contains(word),
                    onCheckedChange = {
                        if (isSelected) {
                            isSelected = false
                            viewModel.onEvent(WordEvent.OnWordUnSelected(word))
                        } else {
                            isSelected = true
                            viewModel.onEvent(WordEvent.OnWordSelected(word))
                        }
                    })
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    fontWeight = FontWeight.Bold
                )
                if (viewModel.state.isMeaningVisible || isMeaningShowing) {
                    Text(text = word.meaning.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    })
                }
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (word.repetitions > 2) {
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









