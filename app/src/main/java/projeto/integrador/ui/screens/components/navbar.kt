import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun NavBar(navController: NavHostController, modifier: Modifier = Modifier) {
    var menuExpanded by remember { mutableStateOf(false) }
    val auth = Firebase.auth

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Ícone de configurações com menu
            Box {
                TextButton(onClick = { menuExpanded = true }) {
                    Image(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configurações"
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Perfil") },
                        onClick = {
                            menuExpanded = false
                            navController.navigate("profile")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Configuração") },
                        onClick = {
                            menuExpanded = false
                            navController.navigate({/*CONFG*/})
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Logout") },
                        onClick = {
                            menuExpanded = false
                            auth.signOut()
                            navController.navigate("signIn")
                        }
                    )
                }
            }

            // Título
            TextButton (onClick = {navController.navigate("home")}){
                Text("Projeto Integrador", textDecoration = TextDecoration.Underline)
            }

            // Ícone de adicionar
            TextButton(onClick = { navController.navigate("adicionarConta") }) {
                Image(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Conta"
                )
            }
        }
    }
}
