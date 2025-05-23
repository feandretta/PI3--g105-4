import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.WindowInsets
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier.fillMaxSize().systemBarsPadding(),
    navController: NavHostController
) {

    val db = Firebase.firestore
    val auth = Firebase.auth
    
    var nomeUsuario by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                errorMessage = "Usuário não autenticado"
                isLoading = false
                return@LaunchedEffect
            }

            val doc = db.collection("usuarios").document(currentUser.uid).get().await()
            nomeUsuario = doc.getString("nome")
            if (nomeUsuario == null) {
                errorMessage = "Nome do usuário não encontrado"
            }
        } catch (e: Exception) {
            Log.e("HomeScreen", "Erro ao obter dados do usuário", e)
            errorMessage = "Erro ao carregar dados: ${e.message}"
        } finally {
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
                            onClick = { navController.navigate("login") }
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

                        HomeOptionCard(
                            icon = Icons.Default.Delete,
                            title = "Deletar Conta",
                            description = "Remover uma conta existente",
                            onClick = { /* TODO: Navigate to delete account */ }
                        )

                        HomeOptionCard(
                            icon = Icons.Default.DateRange,
                            title = "Histórico",
                            description = "Contas adicionadas recentemente",
                            onClick = { /* TODO: Navigate to history */ }
                        )

                        HomeOptionCard(
                            icon = Icons.Default.Settings,
                            title = "Configurações",
                            description = "Ajustar preferências do aplicativo",
                            onClick = { navController.navigate("settings")}
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun  HomeOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}