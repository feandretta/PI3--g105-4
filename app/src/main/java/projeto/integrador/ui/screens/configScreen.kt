// Declaração do pacote
package projeto.integrador.ui.screens

// Imports do Android
import android.app.UiModeManager
import android.os.Build
import android.widget.Toast

// Imports do Jetpack Compose para UI
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import projeto.integrador.ui.screens.components.ConfigCard

// Navegação

// Firebase Authentication
import com.google.firebase.auth.auth

// Componentes locais
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Logout
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.google.firebase.Firebase
import projeto.integrador.utilities.signOut

@Composable
fun ConfigScreen(
    padding: PaddingValues,
    navController: NavController
) {
    val context = LocalContext.current

    var darkMode by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    val auth = Firebase.auth

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "Configurações",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Aparência",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            ConfigCard(
                icon = Icons.Filled.DarkMode,
                title = "Tema Escuro",
                description = "Ativar modo escuro",
                trailingContent = {
                    Switch(
                        checked = darkMode,
                        onCheckedChange = { darkMode = it }
                    )
                }
            )
        }

        item {
            Text(
                text = "Segurança",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            ConfigCard(
                icon = Icons.Filled.Lock,
                title = "Alterar Senha",
                description = "Modificar sua senha de acesso",
                onClick = {
                    auth.currentUser?.email?.let {
                        auth.sendPasswordResetEmail(it)
                        Toast.makeText(
                            context,
                            "Um email de recuperação de senha foi enviado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }

        item {
            Text(
                text = "Notificações",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            ConfigCard(
                icon = Icons.Filled.Notifications,
                title = "Notificações Push",
                description = "Receber alertas e atualizações",
                trailingContent = {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }
            )
        }

        item {
            Text(
                text = "Sobre",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            ConfigCard(
                icon = Icons.Filled.Info,
                title = "Versão",
                description = "1.0.0"
            )
        }

        item {
            ConfigCard(
                icon = Icons.Filled.Description,
                title = "Termos de Uso",
                description = "Leia nossos termos e condições",
                onClick = {
                    Toast.makeText(context, "Termos de Uso em breve", Toast.LENGTH_SHORT).show()
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }

        item {
            ConfigCard(
                icon = Icons.Filled.Description,
                title = "Política de Privacidade",
                description = "Como tratamos seus dados",
                onClick = {
                    Toast.makeText(context, "Política de Privacidade em breve", Toast.LENGTH_SHORT).show()
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }

        item {
            ConfigCard(
                icon = Icons.AutoMirrored.Filled.Logout,
                title = "Deslogar",
                description = "Sai da sua conta no aplicativo",
                onClick = {
                    signOut()
                    navController.navigate("signUp")
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}

