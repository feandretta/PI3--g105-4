// Importações do Android Framework
import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.WindowInsets

// Anotações
import androidx.annotation.RequiresApi

// Imports do Jetpack Compose para UI
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Extensões de compatibilidade
import androidx.core.view.WindowCompat

// Navegação
import androidx.navigation.NavHostController

// Firebase (Autenticação e Banco de Dados)
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Corrotinas
import kotlinx.coroutines.tasks.await

/**
 * Função auxiliar para esconder as barras do sistema (status bar e navigation bar).
 * Isso permite que o conteúdo ocupe toda a tela.
 *
 * @RequiresApi(Build.VERSION_CODES.R) Requer Android 11 (API 30) ou superior
 */
@RequiresApi(Build.VERSION_CODES.R)
@Composable
private fun HideSystemBars() {
    // Obtém a view atual do Compose
    val view = LocalView.current

    // Verifica se não está em modo de pré-visualizaçã
    if (!view.isInEditMode) {
        // SideEffect garante que este código seja executado apenas uma vez por composição
        SideEffect {
            val window = (view.context as Activity).window
            // Faz o conteúdo ocupar toda a área da janela (atrás das barras do sistema)
            WindowCompat.setDecorFitsSystemWindows(window, false)
            // Esconde as barras de status e navegação
            window.insetsController
                ?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
    }
}

/**
 * Tela inicial do aplicativo que exibe opções principais para o usuário.
 *
 * @param modifier Modificador para personalizar o layout da tela
 * @param navController Controlador de navegação para gerenciar a navegação entre telas
 */
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier.fillMaxSize().systemBarsPadding(),
    navController: NavHostController
) {
    // Esconde as barras do sistema para ter uma experiência imersiva
    HideSystemBars()

    // Referências ao Firebase
    val db = Firebase.firestore
    val auth = Firebase.auth

    // Estados da UI
    var nomeUsuario by remember { mutableStateOf<String?>(null) }  // Nome do usuário logado
    var isLoading by remember { mutableStateOf(true) }  // Estado de carregamento
    var errorMessage by remember { mutableStateOf<String?>(null) }  // Mensagem de erro, se houver

    // Efeito colateral para carregar os dados do usuário quando a tela é exibida
    LaunchedEffect(Unit) {
        try {
            // Verifica se há um usuário autenticado
            val currentUser = auth.currentUser
            if (currentUser == null) {
                errorMessage = "Usuário não autenticado"
                isLoading = false
                return@LaunchedEffect
            }

            // Busca os dados do usuário no Firestore
            val doc = db.collection("usuarios").document(currentUser.uid).get().await()
            nomeUsuario = doc.getString("nome")

            // Verifica se o nome do usuário foi encontrado
            if (nomeUsuario == null) {
                errorMessage = "Nome do usuário não encontrado"
            }
        } catch (e: Exception) {
            // Em caso de erro, registra no log e exibe mensagem para o usuário
            Log.e("HomeScreen", "Erro ao obter dados do usuário", e)
            errorMessage = "Erro ao carregar dados: ${e.message}"
        } finally {
            // Marca o carregamento como concluído (seja com sucesso ou erro)
            isLoading = false
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            NavBar(
                navController = navController,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { navController.navigate("signIn") }
                        ) {
                            Text("Ir para Login")
                        }
                    }
                }
                nomeUsuario != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Welcome Text
                        Text(
                            text = "Bem-vindo, $nomeUsuario!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Main Options
                        HomeOptionCard(
                            icon = Icons.Default.AccountCircle,
                            title = "Adicionar Conta",
                            description = "Criar uma nova conta",
                            onClick = { /* TODO: Navigate to add account */ }
                        )

                        HomeOptionCard(
                            icon = Icons.Default.Edit,
                            title = "Editar Conta",
                            description = "Modificar informações da conta",
                            onClick = { /* TODO: Navigate to edit account */ }
                        )

                        // Card para deletar conta
                        HomeOptionCard(
                            icon = Icons.Default.Delete,
                            title = "Deletar Conta",
                            description = "Remover uma conta existente",
                            onClick = { /* TODO: Navegar para tela de deletar conta */ }
                        )

                        HomeOptionCard(
                            icon = Icons.Default.MoreVert,
                            title = "Categorias",
                            description = "Categorias do usuário",
                            onClick = { navController.navigate("categorias")}
                        )

                        HomeOptionCard(
                            icon = Icons.Default.Settings,
                            title = "Configurações",
                            description = "Ajustar preferências do aplicativo",
                            onClick = { navController.navigate("settings")}
                        )

                        // Botão de sair (logout)
                        Button(
                            onClick = {
                                // Realiza o logout e navega para a tela de login
                                Firebase.auth.signOut()
                                navController.navigate("login") {
                                    // Limpa a pilha de navegação para evitar voltar para a tela de home
                                    popUpTo(navController.graph.id) { inclusive = true }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp, vertical = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Sair", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Componente reutilizável que exibe um card de opção na tela inicial.
 *
 * @param icon Ícone a ser exibido no card
 * @param title Título da opção
 * @param description Descrição detalhada da opção
 * @param onClick Callback chamado quando o card é clicado
 */
@Composable
fun HomeOptionCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    // Card que envolve o conteúdo da opção
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Linha que contém o ícone e os textos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Coluna com título e descrição
            Column {
                // Título da opção
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                // Descrição da opção
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}