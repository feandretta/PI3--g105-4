package projeto.integrador.utilities

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarCategoriaDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCategoryCreated: () -> Unit = {}
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(0xFF4CAF50) } // Verde como cor padrão
    val colors = listOf(
        0xFF4CAF50, // Verde
        0xFF2196F3, // Azul
        0xFFF44336, // Vermelho
        0xFFFFC107, // Âmbar
        0xFF9C27B0, // Roxo
        0xFF00BCD4, // Ciano
        0xFFFF9800, // Laranja
        0xFF795548  // Marrom
    )
    val db = Firebase.firestore

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { 
                Text(
                    text = "Nova Categoria",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        label = { Text("Nome") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text(
                        text = "Cor",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Selecione a cor da categoria",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Primeira linha de cores
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        colors.take(4).forEach { color ->
                            ColorCircle(
                                color = Color(color),
                                isSelected = selectedColor == color,
                                onColorSelected = { selectedColor = color }
                            )
                        }
                    }

                    // Segunda linha de cores
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        colors.takeLast(4).forEach { color ->
                            ColorCircle(
                                color = Color(color),
                                isSelected = selectedColor == color,
                                onColorSelected = { selectedColor = color }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        saveCategory(
                            name = categoryName,
                            color = selectedColor,
                            onSuccess = {
                                onCategoryCreated()
                                onDismiss()
                                categoryName = "" // Reset do campo
                                
                            }
                        )
                    },
                    enabled = categoryName.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    onColorSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onColorSelected() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selecionado",
                tint = Color.White
            )
        }
    }
}

/**
 * Salva uma nova categoria como subcoleção do usuário logado
 * @param name Nome da categoria
 * @param color Cor da categoria em formato Long (ex: 0xFF4CAF50)
 * @param onSuccess Callback opcional chamado quando a categoria é salva com sucesso
 */
private fun saveCategory(
    name: String,
    color: Long,
    onSuccess: () -> Unit = {}
) {
    // 1. Obtém o usuário logado
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    
    // 2. Converte a cor para formato hexadecimal
    val hexColor = String.format("#%06X", 0xFFFFFF and color.toInt())
    
    // 3. Cria o objeto da categoria
    val category = hashMapOf(
        "name" to name.trim(),
        "color" to hexColor,
        "createdAt" to Timestamp.now()
    )
    
    // 4. Salva no Firestore
    FirebaseFirestore.getInstance()
        .collection("usuarios")          // Coleção de usuários
        .document(userId)              // Documento do usuário atual
        .collection("categories")      // Subcoleção de categorias
        .add(category)                 // Adiciona o documento
        .addOnSuccessListener {
            // Sucesso: chama o callback
            onSuccess()
        }
        .addOnFailureListener { e ->
            // Em caso de erro, apenas loga no console
            println("Erro ao salvar categoria: ${e.message}")
        }
}