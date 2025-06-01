package projeto.integrador.ui.screens.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RequiredTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        supportingText = { Text("Campo obrigatório") },
        singleLine = true,
        modifier = modifier,
        enabled = enabled
    )
}