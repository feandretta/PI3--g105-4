import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import projeto.integrador.R
import projeto.integrador.utilities.signOut

@Composable
fun NavBar(navController: NavHostController, modifier: Modifier = Modifier) {
    var settingsMenuExpanded by remember { mutableStateOf(false) }
    var addMenuExpanded by remember { mutableStateOf(false) }

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
            // Ícone de configurações com menu
            Box {
                IconButton(onClick = { settingsMenuExpanded = true}) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configurações"
                    )
                }

                DropdownMenu(
                    expanded = settingsMenuExpanded,
                    onDismissRequest = { settingsMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Perfil") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil"
                            )
                        },
                        onClick = {
                            settingsMenuExpanded = false
                            navController.navigate("profile")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Configuração") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configurações"
                            )
                        },
                        onClick = {
                            settingsMenuExpanded = false
                            navController.navigate("settings")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Logout") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Sair"
                            )
                        },
                        onClick = {
                            settingsMenuExpanded = false
                            //função só pra tirar uma chamada do firebase de uma view/componente
                            signOut()
                            navController.navigate("signIn")
                        }
                    )
                }
            }

            // Título
            TextButton(onClick = { navController.navigate("home") }) {
                Text("Projeto Integrador", textDecoration = TextDecoration.Underline)
            }

            // Ícone de adicionar
            Box {
                IconButton(onClick = { addMenuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Adicionar"
                    )
                }

                DropdownMenu(
                    expanded = addMenuExpanded,
                    onDismissRequest = { addMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Nova Categoria") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Adicionar Categoria"
                            )
                        },
                        onClick = {
                            addMenuExpanded = false

                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Filtrar por categoria") },
                        leadingIcon = {
                            Image(
                                painter = painterResource(R.drawable.baseline_filter_alt_24),
                                contentDescription = "Filtrar por categoria"
                            )
                        },
                        onClick = {
                            addMenuExpanded = false
                            // TODO: Implementar ação de filtrar por categoria
                        }
                    )
                }
            }
        }
    }
}
