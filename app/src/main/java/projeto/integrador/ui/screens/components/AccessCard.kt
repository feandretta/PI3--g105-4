package projeto.integrador.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import projeto.integrador.data.model.Access
import projeto.integrador.utilities.CryptoManager

@Composable
fun AccessCard(
    access: Access, // Objeto contendo os dados do acesso
    accessId: String, // ID único do acesso
    onView: (String) -> Unit, // Ação para visualizar detalhes
    onEdit: (String) -> Unit, // Ação para editar
    onDelete: (String) -> Unit // Ação para deletar
) {
    // Estado que controla a exibição do menu suspenso (Dropdown)
    var expanded by remember { mutableStateOf(false) }

    // Cartão visual que exibe os dados do acesso
    Card(
        modifier = Modifier
            .fillMaxWidth() // Ocupa toda a largura disponível
            .wrapContentHeight(), // Altura baseada no conteúdo
        shape = MaterialTheme.shapes.medium, // Arredondamento dos cantos
        elevation = CardDefaults.cardElevation(4.dp) // Sombra do cartão
    ) {
        // Coluna com espaçamento interno
        Column(modifier = Modifier.padding(16.dp)) {
            // Linha principal contendo dados e ícone de opções
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Coluna com nome, email e senha descriptografada
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = access.nome.orEmpty(), // Nome do acesso
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // Espaço entre elementos

                    Text(
                        text = access.email.orEmpty(), // Email relacionado
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp)) // Espaço entre elementos

                    Text(
                        text = CryptoManager.decrypt(access.senha.orEmpty()), // Senha descriptografada
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Caixa com botão de mais opções (ícone de 3 pontinhos)
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Mais opções")
                    }

                    // Menu suspenso com ações (visualizar, editar, deletar)
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        // Ação: Visualizar
                        DropdownMenuItem(
                            text = { Text("Visualizar") },
                            onClick = {
                                expanded = false
                                onView(accessId)
                            }
                        )

                        // Ação: Editar
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            onClick = {
                                expanded = false
                                onEdit(accessId)
                            }
                        )

                        // Ação: Deletar
                        DropdownMenuItem(
                            text = { Text("Deletar") },
                            onClick = {
                                expanded = false
                                onDelete(accessId)
                            }
                        )
                    }
                }
            }
        }
    }
}