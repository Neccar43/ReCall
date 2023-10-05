package com.novacodestudios.recall.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.recall.R
import com.novacodestudios.recall.presentation.util.StandardCircularIndicator
import com.novacodestudios.recall.presentation.util.StandardDialog
import com.novacodestudios.recall.presentation.util.StandardPasswordField
import com.novacodestudios.recall.presentation.util.StandardText
import com.novacodestudios.recall.presentation.util.StandardTextField
import com.novacodestudios.recall.presentation.util.UIText
import com.novacodestudios.recall.util.isNotNull
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToAuthGraph: () -> Unit,
) {
    val state = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    val context= LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest {event->
            when (event) {
                is SettingsViewModel.UIEvent.SignOut -> {
                    onNavigateToAuthGraph()
                }

                is SettingsViewModel.UIEvent.ShowSnackbar -> {
                    when (val text=event.message) {
                        is UIText.DynamicText ->  snackbarHostState.showSnackbar(text.value)
                        is UIText.StringResource -> snackbarHostState.showSnackbar(text.asString(context = context))
                    }
                }
            }
        }
    }
    Scaffold(
        snackbarHost = {SnackbarHost(snackbarHostState)}
    ) {paddingValue->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValue),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val dividerModifier = Modifier.padding(horizontal = 0.dp)
            val textModifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 15.dp)

            val textSize = MaterialTheme.typography.titleMedium.fontSize

            /*StandardText(
                text = stringResource(id = R.string.my_account),
                modifier = textModifier.clickable { },
                imageVector = Icons.Outlined.Person,
                description = stringResource(id = R.string.my_account),
                fontSize = textSize
            )

            Divider(modifier = dividerModifier)*/

            Row(
                modifier = textModifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StandardText(
                    text = stringResource(id = R.string.dark_theme),
                    imageVector = Icons.Outlined.DarkMode,
                    description = stringResource(id = R.string.dark_theme), fontSize = textSize
                )

                Switch(
                    checked = state.isDarkTheme,
                    onCheckedChange = {
                        viewModel.onEvent(SettingsEvent.ThemeChanged(!state.isDarkTheme))
                    },
                    modifier = Modifier
                        .height(14.dp)
                        .padding(end = 16.dp)
                )
            }
            Divider(modifier = dividerModifier)

            Row(
                modifier = textModifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StandardText(
                    text = stringResource(id = R.string.meanings_invisible),
                    imageVector = Icons.Outlined.VisibilityOff,
                    description = stringResource(id = R.string.meanings_invisible),
                    fontSize = textSize
                )

                Switch(
                    checked = !state.isMeaningVisible,
                    onCheckedChange = {
                        viewModel.onEvent(SettingsEvent.OnMeaningVisibilityChanged(!state.isMeaningVisible))
                    },
                    modifier = Modifier
                        .height(14.dp)
                        .padding(end = 16.dp)
                )
            }

            Divider(modifier = dividerModifier)

            /*Row(
                modifier = textModifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StandardText(
                    text = stringResource(id = R.string.hide_mastered_words),
                    imageVector = Icons.Outlined.LocalFireDepartment,
                    description = stringResource(id = R.string.hide_mastered_words),
                    fontSize = textSize
                )

                Switch(
                    checked = false,
                    onCheckedChange = {

                    },// TODO: Bu kısmı sonra tamamla
                    modifier = Modifier
                        .height(14.dp)
                        .padding(end = 16.dp)

                )
            }

            Divider(modifier = dividerModifier)*/

            StandardText(
                text = stringResource(id = R.string.delete_account),
                modifier = textModifier.clickable { viewModel.onEvent(SettingsEvent.DialogVisibilityChanged) },
                imageVector = Icons.Outlined.Delete,
                description = stringResource(id = R.string.delete_account),
                fontSize = textSize
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
                fontSize = textSize
            )

            Divider(modifier = dividerModifier)

            if (visible)
                StandardDialog(title = stringResource(id = R.string.sign_out_dialog_title),
                    onDismiss = { visible = false }, onRequest = {
                        viewModel.onEvent(SettingsEvent.SignOut)
                        visible = false
                    }) {
                    StandardText(
                        text = stringResource(id = R.string.exit_app),
                        modifier = Modifier,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )

                }



            StandardCircularIndicator(isLoading = state.isLoading)

            if (state.isDeleteDialogVisible) {
                StandardDialog(
                    title = stringResource(id = R.string.delete_account),
                    onDismiss = { viewModel.onEvent(SettingsEvent.DialogVisibilityChanged)},
                    onRequest = { viewModel.onEvent(SettingsEvent.DeleteAccount) }) {
                    StandardTextField(
                        hint = stringResource(id = R.string.email),
                        iconStart = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = stringResource(id = R.string.email)
                            )
                        },
                        keyboardType = KeyboardType.Email,
                        text = state.email,
                        onValueChange = { viewModel.onEvent(SettingsEvent.EmailChanged(it)) },
                        isError = state.emailError.isNotNull(),
                        supportingText = state.emailError?.asString()
                    )

                    StandardPasswordField(
                        hint = stringResource(id = R.string.password),
                        iconStart = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = stringResource(id = R.string.password)
                            )
                        },
                        onValueChange = { viewModel.onEvent(SettingsEvent.PasswordChanged(it)) },
                        text = state.password,
                        isError = state.passwordError.isNotNull(),
                        supportingText = state.passwordError?.asString()
                    )

                }
            }
        }
    }

}

