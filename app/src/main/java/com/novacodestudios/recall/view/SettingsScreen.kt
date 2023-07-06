package com.novacodestudios.recall.view

import android.content.res.Configuration
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.novacodestudios.recall.Graph
import com.novacodestudios.recall.util.StandardDialog
import com.novacodestudios.recall.util.StandardText
import com.novacodestudios.recall.ui.theme.ReCallTheme
import com.novacodestudios.recall.viewmodel.QuizHistoryViewModel
import com.novacodestudios.recall.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(navController: NavController,viewModel: SettingsViewModel = hiltViewModel()) {
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
                text = "Hesabım",
                modifier = textModifier,
                imageVector = Icons.Outlined.Person,
                description = "Hesabım"
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
                    text = "Koyu tema",
                    imageVector = Icons.Outlined.DarkMode,
                    description = "Koyu mod"
                )

                var checked by remember {
                    mutableStateOf(false)
                }
                Switch(
                    checked = checked, onCheckedChange = { checked = it },
                    modifier = Modifier
                        .height(14.dp)
                        .padding(end = 20.dp)
                )
            }


            Divider(modifier = dividerModifier)

            StandardText(
                text = "Hesabı sil", modifier = textModifier,
                imageVector = Icons.Outlined.Delete,
                description = "Hesabı sil"
            )

            Divider(modifier = dividerModifier)
            var visible by remember {
                mutableStateOf(false)
            }
            StandardText(
                text = "Çıkış yap",
                modifier = textModifier.clickable { visible = true },
                imageVector = Icons.Outlined.Logout,
                description = "Çıkış yap",
            )

            Divider(modifier = dividerModifier)

            if (visible)
                StandardDialog(title = "Çıkış?", onDismiss = { visible = false }, onRequest = {
                    navController.navigate(Graph.AUTHENTICATION) {
                        popUpTo(Graph.HOME) {
                            inclusive = true
                        }
                    }
                    visible = false
                }) {
                    Spacer(modifier = Modifier.height(10.dp))
                    StandardText(
                        text = "Uygulamadan çıkış yapmak üzeresiniz.",
                        modifier = Modifier,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )

                }


        }
    }
}


@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewSettingsScreen() {
    ReCallTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SettingsScreen(navController = rememberNavController())
        }
    }
}
