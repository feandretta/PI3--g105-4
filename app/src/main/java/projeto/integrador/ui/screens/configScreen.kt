// Declaração do pacote
package projeto.integrador.ui.screens

// Imports necessários
import android.app.UiModeManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import projeto.integrador.ui.screens.components.ConfigCard
import projeto.integrador.utilities.signOut

@Composable
fun ConfigScreen(
    padding: PaddingValues,
    navController: NavController
) {
    val context = LocalContext.current

    // Estados locais para switches
    var darkMode by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    val auth = Firebase.auth // Referência à instância do Firebase Auth

    // Lista vertical com padding e espaçamento entre itens
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Título principal
        item {
            Text(
                text = "Configurações",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        // Seção de aparência
        item {
            Text(
                text = "Aparência",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Switch do modo escuro
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

        // Seção de segurança
        item {
            Text(
                text = "Segurança",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Alterar senha (via email)
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

        // Seção de notificações
        item {
            Text(
                text = "Notificações",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Switch de notificações push
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

        // Seção "Sobre"
        item {
            Text(
                text = "Sobre",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Versão do app
        item {
            ConfigCard(
                icon = Icons.Filled.Info,
                title = "Versão",
                description = "1.0.0"
            )
        }

        // Termos de uso
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

        // Política de privacidade
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

        // Logout da conta
        item {
            ConfigCard(
                icon = Icons.AutoMirrored.Filled.Logout,
                title = "Deslogar",
                description = "Sai da sua conta no aplicativo",
                onClick = {
                    signOut() // Encerra a sessão do usuário
                    navController.navigate("signUp") // Volta para tela de cadastro/login
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
