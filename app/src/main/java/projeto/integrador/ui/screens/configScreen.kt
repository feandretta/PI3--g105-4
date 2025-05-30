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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.firebase.Firebase

@Composable
fun ConfigScreen(
    modifier: Modifier = Modifier.fillMaxSize().systemBarsPadding(),
) {

    // Contexto atual da aplicação
    val context = LocalContext.current

    // Estados para as configurações do usuário
    var darkMode by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf("Português") }

    val auth = Firebase.auth


    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Configurações",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Aparência",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
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

            Text(
                text = "Segurança",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
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
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            Text(
                text = "Notificações",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
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

            Text(
                text = "Sobre",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            ConfigCard(
                icon = Icons.Filled.Info,
                title = "Versão",
                description = "1.0.0"
            )
            ConfigCard(
                icon = Icons.Filled.Description,
                title = "Termos de Uso",
                description = "Leia nossos termos e condições",
                onClick = {
                    Toast.makeText(context, "Termos de Uso em breve", Toast.LENGTH_SHORT).show()
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
            ConfigCard(
                icon = Icons.Filled.Description,
                title = "Política de Privacidade",
                description = "Como tratamos seus dados",
                onClick = {
                    Toast.makeText(context, "Política de Privacidade em breve", Toast.LENGTH_SHORT)
                        .show()
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
