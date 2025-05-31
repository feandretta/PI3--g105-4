package projeto.integrador.utilities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        saveCategory(
                            name = categoryName,
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

/**
 * Salva uma nova categoria como subcoleção do usuário logado
 * @param name Nome da categoria
 * @param onSuccess Callback opcional chamado quando a categoria é salva com sucesso
 */
private fun saveCategory(
    name: String,
    onSuccess: () -> Unit = {}
) {
    // 1. Obtém o usuário logado
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return


    // 3. Cria o objeto da categoria
    val category = hashMapOf(
        "name" to name.trim(),
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