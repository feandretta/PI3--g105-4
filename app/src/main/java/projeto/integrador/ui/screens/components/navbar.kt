package projeto.integrador.ui.screens.components// ... (imports existentes)
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import projeto.integrador.utilities.CriarCategoriaDialog

@Composable
fun NavBar(navController: NavHostController, modifier: Modifier = Modifier) {
    var settingsMenuExpanded by remember { mutableStateOf(false) }
    var addMenuExpanded by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) } // Estado para controlar o diálogo
    val auth = Firebase.auth
    val db = Firebase.firestore

    // Adiciona o diálogo de categoria
    CriarCategoriaDialog(
        showDialog = showCategoryDialog,
        onDismiss = { showCategoryDialog = false },
        onCategoryCreated = {
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth().safeContentPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Menu de configurações
            Box {
                IconButton(onClick = { settingsMenuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu"
                    )
                }
                
                DropdownMenu(
                    expanded = settingsMenuExpanded,
                    onDismissRequest = { settingsMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Configurações") },
                        onClick = { 
                            navController.navigate("settings")
                            settingsMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Sair") },
                        onClick = { 
                            auth.signOut()
                            navController.navigate("signIn") {
                                popUpTo(0) { inclusive = true }
                            }
                            settingsMenuExpanded = false
                        }
                    )
                }
            }

            // Logo ou título do app
            TextButton(
                onClick = ({navController.navigate("home")}),

            ){
                Text("Super ID",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                    )
        }

            // Botão de adicionar
            Box {
                IconButton(onClick = { addMenuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar"
                    )
                }
                
                DropdownMenu(
                    expanded = addMenuExpanded,
                    onDismissRequest = { addMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Nova Categoria") },
                        onClick = {
                            showCategoryDialog = true
                            addMenuExpanded = false
                        }
                    )
//                    DropdownMenuItem(
//                        text = { Text("Filtrar por categoria")},
//                        onClick = {
//                            navController.navigate("filtrar")
//                            addMenuExpanded = false
//                        }
//                    )
                }
            }
        }
    }
}