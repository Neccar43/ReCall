package com.novacodestudios.recall.presentation.sign_in

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    onNavigateToMainGraph: () -> Unit,
    onNavigateToSignUpScreen: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SignInViewModel.UIEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is SignInViewModel.UIEvent.SignIn -> {
                    onNavigateToMainGraph()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val state = viewModel.state
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)

            ) {
                val standardModifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)

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
                    text = state.email,
                    onValueChange = { viewModel.onEvent(SignInEvent.EmailChanged(it)) },
                    isError = state.emailError.isNotNull(),
                    supportingText = state.emailError
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
                    onValueChange = { viewModel.onEvent(SignInEvent.PasswordChanged(it)) },
                    text = state.password,
                    isError = state.passwordError.isNotNull(),
                    supportingText = state.passwordError
                )
                StandardLinkedText(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 8.dp, bottom = 8.dp),
                    text = stringResource(id = R.string.forget_password),
                    onClick = { })

                StandardButton(
                    onClick = { viewModel.onEvent(SignInEvent.SignIn) },
                    text = stringResource(id = R.string.sign_in),
                    modifier = standardModifier
                )

                StandardLinkedText(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(end = 8.dp, bottom = 8.dp),
                    text = stringResource(id = R.string.do_not_have_account),
                    onClick = {
                        onNavigateToSignUpScreen()
                    })
            }
            StandardCircularIndicator(isLoading = state.isLoading)

        }
        

    }

}


