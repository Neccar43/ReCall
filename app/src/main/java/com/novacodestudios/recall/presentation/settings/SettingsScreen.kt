package com.novacodestudios.recall.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.recall.R
import com.novacodestudios.recall.presentation.util.StandardCircularIndicator
import com.novacodestudios.recall.presentation.util.StandardDialog
import com.novacodestudios.recall.presentation.util.StandardText
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToAuthGraph:()->Unit,
    ) {
    val state = viewModel.state
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest {
            when (it) {
               is SettingsViewModel.UIEvent.SignOut -> {
                  onNavigateToAuthGraph()
               }
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(top = 10.dp),

            ) {
            val dividerModifier = Modifier.padding(horizontal = 8.dp)
            val textModifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 15.dp)

            StandardText(
                text = stringResource(id = R.string.my_account),
                modifier = textModifier.clickable {  },
                imageVector = Icons.Outlined.Person,
                description = stringResource(id = R.string.my_account)
            )

            Divider(modifier = dividerModifier)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StandardText(
                    text = stringResource(id = R.string.dark_theme),
                    imageVector = Icons.Outlined.DarkMode,
                    description = stringResource(id = R.string.dark_theme)
                )

                Switch(
                    checked = state.isDarkTheme,
                    onCheckedChange = {
                        viewModel.onEvent(SettingsEvent.ThemeChanged(!state.isDarkTheme))
                    },
                    modifier = Modifier
                        .height(14.dp)
                        .padding(end = 20.dp)
                )
            }

            Divider(modifier = dividerModifier)

            StandardText(
                text = stringResource(id = R.string.delete_account), modifier = textModifier,
                imageVector = Icons.Outlined.Delete,
                description = stringResource(id = R.string.delete_account)
            )

            Divider(modifier = dividerModifier)
            var visible by remember {
                mutableStateOf(false)
            }
            StandardText(
                text = stringResource(id = R.string.sign_out),
                modifier = textModifier.clickable { visible = true },
                imageVector = Icons.Outlined.Logout,
                description = stringResource(id = R.string.sign_out),
            )

            Divider(modifier = dividerModifier)

            if (visible)
                StandardDialog(title = stringResource(id = R.string.are_you_sure),
                    onDismiss = { visible = false }, onRequest = {
                        viewModel.onEvent(SettingsEvent.SignOut)
                        visible = false
                    }) {
                    Spacer(modifier = Modifier.height(10.dp))
                    StandardText(
                        text = stringResource(id = R.string.exit_app),
                        modifier = Modifier,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )

                }


        }
        StandardCircularIndicator(isLoading = state.isLoading)
    }
}

