package projeto.integrador.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Um componente de cartão reutilizável para telas de configuração ou listas com ícones, título e descrição.
 *
 * @param icon Ícone exibido no início da linha.
 * @param title Texto de título principal.
 * @param description Texto de descrição secundária.
 * @param trailingContent Conteúdo opcional exibido à direita (ex: um switch ou seta).
 * @param onClick Ação opcional ao clicar no cartão.
 */
@Composable
fun ConfigCard(
    icon: ImageVector,
    title: String,
    description: String,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {

    // Define o modifier base do card, com preenchimento e largura total.
    // Se onClick for fornecido, o card será clicável.
    val modifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    }

    // Organiza os elementos do card em linha: ícone, textos e conteúdo opcional à direita.
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícone da esquerda
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )

        // Espaçamento entre ícone e textos
        Spacer(modifier = Modifier.width(16.dp))

        // Coluna com título e descrição
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Se houver conteúdo opcional à direita (como um Switch, Toggle ou Icon), ele será exibido aqui
        if(trailingContent != null)
            trailingContent()
    }
}
