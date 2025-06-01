package projeto.integrador.ui.screens.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuAnchorType.Companion.PrimaryEditable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorField(
    modifier: Modifier = Modifier, // Modificador externo para aplicar estilo (opcional)
    label: String, // Rótulo do campo
    selectedItem: String, // Item atualmente selecionado
    items: List<String>, // Lista de opções disponíveis
    onItemSelected: (String) -> Unit, // Função chamada quando um item é selecionado
    enabled: Boolean = true // Define se o campo está habilitado
) {
    // Estado interno que controla se o menu está expandido ou não
    var expanded by remember { mutableStateOf(false) }

    // Estrutura do campo com menu suspenso embutido (componente do Material 3)
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded } // Alterna o estado de visibilidade do menu
    ) {
        // Campo de texto apenas leitura com aparência de drop-down
        OutlinedTextField(
            readOnly = true, // Não permite digitação direta
            value = selectedItem, // Mostra o item selecionado
            onValueChange = {}, // Ignorado pois o campo é somente leitura
            label = { Text(label) }, // Rótulo do campo
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = modifier.menuAnchor(PrimaryEditable, enabled).fillMaxWidth(), // Alinha corretamente com o menu
            enabled = enabled // Define se está habilitado
        )

        // Menu suspenso que aparece ao clicar no campo
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false } // Fecha o menu se clicar fora
        ) {
            // Para cada item da lista, cria uma opção no menu
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) }, // Texto da opção
                    onClick = {
                        expanded = false // Fecha o menu ao clicar
                        onItemSelected(item) // Notifica o item selecionado
                    }
                )
            }
        }
    }
}
