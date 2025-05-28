// Declaração do pacote
package projeto.integrador.ui.screens

// Imports do Android
import android.app.Activity
import android.os.Build
import android.view.WindowInsets
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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Navegação
import androidx.navigation.NavHostController

// Firebase Authentication
import com.google.firebase.auth.auth

// Componentes locais
import NavBar
import androidx.annotation.RequiresApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.google.firebase.Firebase

/**
 * Função para ocultar as barras do sistema (status bar e navigation bar).
 * Esta função é compatível apenas com Android R (API 30) e superiores.
 */
@RequiresApi(Build.VERSION_CODES.R)
@Composable
private fun HideSystemBars() {
    val view = LocalView.current
    // Verifica se não está em modo de pré-visualização do Compose
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Configura a janela para desenhar atrás das barras do sistema
            WindowCompat.setDecorFitsSystemWindows(window, false)
            // Oculta as barras de status e navegação
            window.insetsController
                ?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
    }
}

/**
 * Tela de configurações do aplicativo.
 * Permite que os usuários personalizem as configurações do aplicativo.
 *
 * @param modifier Modificador para personalizar o layout da tela
 * @param navController Controlador de navegação para gerenciar a navegação entre telas
 */
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun ConfigScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    navController: NavHostController
) {
    // Oculta as barras do sistema
    HideSystemBars()

    // Contexto atual da aplicação
    val context = LocalContext.current

    // Estados para as configurações do usuário
    var darkMode by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf("Português") }

    // Instância do Firebase Auth
    val auth = Firebase.auth

    Scaffold(
        modifier = modifier,
        topBar = {
            NavBar(
                navController = navController,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título da tela
            Text(
                text = "Configurações",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Seção de Aparência
            ConfigSection(title = "Aparência") {
                // Tema Escuro
                ConfigSwitch(
                    icon = Icons.Filled.DarkMode,
                    title = "Tema Escuro",
                    description = "Ativar modo escuro",
                    checked = darkMode,
                    onCheckedChange = { darkMode = it }
                )
            }

            // Seção de Segurança
            ConfigSection(title = "Segurança") {
                // Alterar Senha
                ConfigButton(
                    icon = Icons.Filled.Lock,
                    title = "Alterar Senha",
                    description = "Modificar sua senha de acesso",
                    onClick = {
                        auth.sendPasswordResetEmail(auth.currentUser?.email.toString())
                        Toast.makeText(context, "Um email de recuperação de senha foi enviado", Toast.LENGTH_SHORT).show()
                    }

                )
            }

            // Seção de Notificações
            ConfigSection(title = "Notificações") {
                // Notificações Push
                ConfigSwitch(
                    icon = Icons.Filled.Notifications,
                    title = "Notificações Push",
                    description = "Receber alertas e atualizações",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }

            // Seção de Sobre
            ConfigSection(title = "Sobre") {
                // Versão do App
                ConfigInfo(
                    icon = Icons.Filled.Info,
                    title = "Versão",
                    description = "1.0.0"
                )

                // Termos de Uso
                ConfigButton(
                    icon = Icons.Filled.Description,
                    title = "Termos de Uso",
                    description = "Leia nossos termos e condições",
                    onClick = {
                        Toast.makeText(context, "Termos de Uso em breve", Toast.LENGTH_SHORT).show()
                    }
                )

                // Política de Privacidade
                ConfigButton(
                    icon = Icons.Filled.Description,
                    title = "Política de Privacidade",
                    description = "Como tratamos seus dados",
                    onClick = {
                        Toast.makeText(context, "Política de Privacidade em breve", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

/**
 * Componente que representa uma seção de configuração com título e conteúdo.
 *
 * @param title Título da seção
 * @param content Conteúdo da seção (componíveis filhos)
 */
@Composable
private fun ConfigSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Título da seção
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Card que contém os itens da seção
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            // Conteúdo da seção com espaçamento entre os itens
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * Componente de switch para configurações booleanas.
 *
 * @param icon Ícone do item
 * @param title Título do item
 * @param description Descrição do item
 * @param checked Estado atual do switch
 * @param onCheckedChange Callback chamado quando o estado do switch muda
 */
@Composable
private fun ConfigSwitch(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // Layout em linha para o item de configuração
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Lado esquerdo: ícone e textos
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Ícone do item
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Título e descrição
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        // Lado direito: switch
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun ConfigButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
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
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = "Ir para",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ConfigInfo(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
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
    }
}

@Composable
private fun ConfigDropdown(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    currentValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
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
        Text(
            text = currentValue,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = "Selecionar",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                text = { Text(option) },
                onClick = {
                    onOptionSelected(option)
                    expanded = false
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Preview
@Composable
fun ConfigScreenPreview() {
    ConfigScreen(navController = NavHostController(LocalContext.current))
}