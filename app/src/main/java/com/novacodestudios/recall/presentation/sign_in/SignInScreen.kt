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
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.novacodestudios.recall.presentation.Graph
import com.novacodestudios.recall.presentation.util.Screen
import com.novacodestudios.recall.util.StandardButton
import com.novacodestudios.recall.util.StandardLinkedText
import com.novacodestudios.recall.util.StandardPasswordField
import com.novacodestudios.recall.util.StandardTextField

@Composable
fun SignInScreen(navController: NavController,viewModel: SignInViewModel = hiltViewModel()) {
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
                hint = "E-Mail",
                modifier = standardModifier,
                iconStart = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "email"
                    )
                },
                keyboardType = KeyboardType.Email
            )

            StandardPasswordField(
                hint = "Şifre",
                modifier = standardModifier,
                iconStart = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "şifre"
                    )
                })
            StandardLinkedText(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 8.dp, bottom = 8.dp),
                text = "Şifrenizi mi unuttunuz?",
                onClick = { })

            StandardButton(
                onClick = {
                          navController.navigate(Graph.HOME){
                              popUpTo(Graph.AUTHENTICATION){
                                  inclusive=true
                              }
                          }
                },
                text = "Giriş yap",
                modifier = standardModifier
            )

            StandardLinkedText(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(end = 8.dp, bottom = 8.dp),
                text = "Hesabınız yok mu? Kaydolun.",
                onClick = {
                    navController.navigate(Screen.SignUpScreen.route)
                })
        }
    }
}