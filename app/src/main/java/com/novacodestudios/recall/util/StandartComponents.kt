package com.novacodestudios.recall.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun StandardTextField(
    modifier: Modifier = Modifier,
    hint: String = "",
    iconStart: @Composable (() -> Unit)? = null,
    iconEnd: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    cornerRadius: Int = 40,
    keyboardType: KeyboardType = KeyboardType.Text,
    getText: (String) -> Unit = {}

) {
    var text by remember {
        mutableStateOf("")
    }
    getText(text)
    OutlinedTextField(
        value = text,
        onValueChange = { str ->
            text = if (keyboardType == KeyboardType.Text)
                str.filter { it.isLetter() }
            else
                str

        },
        modifier = modifier,
        label = { Text(text = hint) },
        leadingIcon = iconStart,
        trailingIcon = iconEnd,
        isError = isError,
        supportingText = {
            if (supportingText != null) {
                Text(text = supportingText)
            }
        },
        singleLine = true,
        maxLines = 1,
        shape = RoundedCornerShape(cornerRadius),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)

    )

}

@Composable
fun StandardPasswordField(
    modifier: Modifier = Modifier,
    hint: String = "",
    iconStart: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    cornerRadius: Int = 40,

    ) {
    var text by remember {
        mutableStateOf("")
    }
    var isPasswordVisible by remember {
        mutableStateOf(false)
    }
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        modifier = modifier,
        label = { Text(text = hint) },
        leadingIcon = iconStart,
        trailingIcon = {
            val iconImage =
                if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
            val description =
                if (isPasswordVisible) "Hide password" else "Show password"

            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(imageVector = iconImage, contentDescription = description)
            }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = isError,
        supportingText = {
            if (supportingText != null) {
                Text(text = supportingText)
            }
        },
        singleLine = true,
        maxLines = 1,
        shape = RoundedCornerShape(cornerRadius),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)

    )

}

@Composable
fun StandardLinkedText(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        modifier = modifier.clickable { onClick() },
        color = MaterialTheme.colorScheme.primary,
        textDecoration = TextDecoration.Underline
    )

}

@Composable
fun StandardButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true
) {
    Button(onClick = { onClick() }, modifier = modifier, enabled = enabled) {
        Text(text = text)
    }
}

@Composable
fun StandardText(
    text: String,
    modifier: Modifier = Modifier,
    imageVector: ImageVector?= null,
    description: String? = null,
    fontSize: TextUnit = MaterialTheme.typography.headlineSmall.fontSize
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        if (imageVector != null) {
            Icon(
                imageVector =imageVector ,
                contentDescription = description,
                modifier = Modifier.padding(end = 10.dp)
            )
        }
        Text(
            text = text,
            fontSize = fontSize
        )

    }

}

@Composable
fun StandardSearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    isError: Boolean = false,
    cornerRadius: Int = 40,
    keyboardType: KeyboardType = KeyboardType.Text,
    onSearch: (String) -> Unit = {}

) {

    var text by remember {
        mutableStateOf("")
    }
    OutlinedTextField(
        value = text,
        onValueChange = { str ->
            text = if (keyboardType == KeyboardType.Text)
                str.filter { it.isLetter() }
            else
                str

        },
        modifier = modifier,
        label = { Text(text = hint) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = hint
            )
        },
        isError = isError,
        singleLine = true,
        maxLines = 1,
        shape = RoundedCornerShape(cornerRadius),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        keyboardActions = KeyboardActions(onDone = { onSearch(text) })

    )

}

@Composable
fun StandardDivider(text: String = "") {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        val dividerModifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        Divider(
            modifier = dividerModifier, thickness = 1.dp,
        )
        if (text.isNotEmpty()) Text(
            text = text,
            fontSize = 16.sp,
            modifier = Modifier.padding(8.dp)
        )
        Divider(dividerModifier, thickness = 1.dp)
    }
}

@Composable
fun StandardDialog(
    title: String,
    dismissText: String = "İptal",
    requestText: String = "Tamam",
    onDismiss: () -> Unit,
    onRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                StandardText(text = title, modifier = Modifier.padding(top = 8.dp))
                content()
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = dismissText)
                    }
                    TextButton(onClick = onRequest) {
                        Text(text = requestText)
                    }
                }
            }

        }
    }


}