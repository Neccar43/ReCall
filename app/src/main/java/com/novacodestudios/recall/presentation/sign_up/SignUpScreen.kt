package com.novacodestudios.recall.presentation.sign_up

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novacodestudios.recall.R
import com.novacodestudios.recall.presentation.util.StandardButton
import com.novacodestudios.recall.presentation.util.StandardCircularIndicator
import com.novacodestudios.recall.presentation.util.StandardLinkedText
import com.novacodestudios.recall.presentation.util.StandardPasswordField
import com.novacodestudios.recall.presentation.util.StandardTextField
import com.novacodestudios.recall.util.isNotNull
import kotlinx.coroutines.flow.collectLatest


@Composable
fun SignUpScreen(
    onNavigateToSignInScreen: () -> Unit,
    onNavigateToHomeGraph: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state = viewModel.state

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SignUpViewModel.UIEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is SignUpViewModel.UIEvent.SignUp -> {
                    onNavigateToHomeGraph()
                }
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        val primaryColor = MaterialTheme.colorScheme.primary
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .drawBehind {
                    drawCircle(
                        color = primaryColor,
                        radius = 1100f,
                        center = center
                    )
                }
                .background(primaryColor)
        )
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)

                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                    ) {
                        Text(
                            text = stringResource(id = R.string.sign_up_title),
                            modifier = Modifier.padding(15.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize
                        )

                        val standardModifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                        StandardTextField(
                            hint = stringResource(id = R.string.name),
                            modifier = standardModifier,
                            iconStart = {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = stringResource(id = R.string.name)
                                )
                            },
                            supportingText = state.fullNameError,
                            isError = state.fullNameError.isNotNull(),
                            text = state.fullName,
                            onValueChange = { viewModel.onEvent(SignUpEvent.FullNameChanged(it)) }
                        )

                        StandardTextField(
                            hint = stringResource(id = R.string.email),
                            modifier = standardModifier,
                            iconStart = {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = stringResource(id = R.string.email)
                                )
                            },
                            keyboardType = KeyboardType.Email,
                            supportingText = state.emailError,
                            isError = state.emailError.isNotNull(),
                            text = state.email,
                            onValueChange = { viewModel.onEvent(SignUpEvent.EmailChanged(it)) }
                        )

                        StandardPasswordField(
                            hint = stringResource(id = R.string.password),
                            modifier = standardModifier,
                            iconStart = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = stringResource(id = R.string.password)
                                )
                            },
                            supportingText = state.passwordError,
                            isError = state.passwordError.isNotNull(),
                            text = state.password,
                            onValueChange = { viewModel.onEvent(SignUpEvent.PasswordChanged(it)) }
                        )
                        StandardPasswordField(
                            hint = stringResource(id = R.string.repeat_password),
                            modifier = standardModifier,
                            iconStart = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = stringResource(id = R.string.repeat_password)
                                )
                            },
                            supportingText = state.repeatedPasswordError,
                            isError = state.repeatedPasswordError.isNotNull(),
                            text = state.repeatedPassword,
                            onValueChange = {
                                viewModel.onEvent(
                                    SignUpEvent.RepeatedPasswordChanged(
                                        it
                                    )
                                )
                            }
                        )


                        StandardButton(
                            onClick = { viewModel.onEvent(SignUpEvent.SignUp) },
                            text = stringResource(id = R.string.sign_up),
                            modifier = standardModifier.padding(vertical = 10.dp)
                        )
                        Row(modifier = Modifier.padding(bottom = 16.dp)) {
                            Text(
                                text = stringResource(id = R.string.already_member),
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize
                            )
                            StandardLinkedText(
                                modifier = Modifier
                                    .padding(end = 8.dp, bottom = 8.dp),
                                text = stringResource(id = R.string.sign_in),
                                onClick = {
                                    onNavigateToSignInScreen()
                                })
                        }
                    }


                }
                StandardCircularIndicator(isLoading = state.isLoading)
            }
        }


    }

}