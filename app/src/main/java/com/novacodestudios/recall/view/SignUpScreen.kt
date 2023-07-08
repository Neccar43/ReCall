package com.novacodestudios.recall.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.novacodestudios.recall.Screen
import com.novacodestudios.recall.ui.theme.ReCallTheme
import com.novacodestudios.recall.util.StandardButton
import com.novacodestudios.recall.util.StandardDivider
import com.novacodestudios.recall.util.StandardLinkedText
import com.novacodestudios.recall.util.StandardPasswordField
import com.novacodestudios.recall.util.StandardTextField
import com.novacodestudios.recall.viewmodel.QuizHistoryViewModel
import com.novacodestudios.recall.viewmodel.SignUpViewModel

@Preview(showSystemUi = true)
@Composable
fun PreviewSignUpScreen() {
    ReCallTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SignUpScreen(navController = rememberNavController())
        }
    }
}

@Composable
fun SignUpScreen(navController: NavController, viewModel: SignUpViewModel = hiltViewModel()) {
    val state = viewModel.state.value
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)

        ) {
            val standardModifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
            StandardTextField(
                hint = "Adınız",
                modifier = standardModifier,
                iconStart = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "adınız"
                    )
                },
                getText = { name = it },
                supportingText = state.error,
                isError = state.error.isNotEmpty()
            )
            StandardTextField(
                hint = "Soyadınız",
                modifier = standardModifier,
                iconStart = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "soyadınız"
                    )
                },
                getText = { surname = it },
                supportingText = state.error,
                isError = state.error.isNotEmpty()
            )

            StandardTextField(
                hint = "E-Mail",
                modifier = standardModifier,
                iconStart = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "email"
                    )
                },
                keyboardType = KeyboardType.Email,
                getText = { email = it },
                supportingText = state.error,
                isError = state.error.isNotEmpty()
            )

            StandardPasswordField(
                hint = "Şifre",
                modifier = standardModifier,
                iconStart = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "şifre"
                    )
                },
                getPassword = { password = it },
                supportingText = state.error,
                isError = state.error.isNotEmpty()
            )
            StandardPasswordField(
                hint = "Şifre (tekrar)",
                modifier = standardModifier,
                iconStart = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "şifre"
                    )
                },
                getPassword = { confirmPassword = it },
                supportingText = state.error,
                isError = state.error.isNotEmpty()
            )


            StandardButton(
                onClick = { viewModel.signUpUser(name, surname, email, password, confirmPassword) },
                text = "Kaydol",
                modifier = standardModifier
            )

            StandardLinkedText(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(end = 8.dp, bottom = 8.dp),
                text = "Zaten üye misiniz? Giriş yapın.",
                onClick = {
                    navController.navigate(Screen.SignInScreen.route) {
                        popUpTo(Screen.SignInScreen.route) {
                            inclusive = true
                        }
                    }
                })

            StandardDivider(text = "Yada")
        }
    }
}