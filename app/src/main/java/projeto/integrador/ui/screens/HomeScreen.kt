import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.WindowInsets
import android.widget.Space
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import androidx.compose.material3.CardDefaults


/** Composable helper para esconder status/navigation bars **/
@RequiresApi(Build.VERSION_CODES.R)
@Composable
private fun HideSystemBars() {
    val view = LocalView.current
    // não executa no preview
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // faz o conteúdo ocupar toda a área
            WindowCompat.setDecorFitsSystemWindows(window, false)
            // esconde status e nav bars
            window.insetsController
                ?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    navController: NavHostController
) {
    // já esconde as system bars
    HideSystemBars()

    val db   = Firebase.firestore
    val auth = Firebase.auth
    val uid  = auth.currentUser?.uid ?: return

    var nomeUsuario by remember { mutableStateOf<String?>(null) }
    var isLoading   by remember { mutableStateOf(true) }

    LaunchedEffect(uid) {
        try {
            val doc = db.collection("usuarios").document(uid).get().await()
            nomeUsuario = doc.getString("nome")
        } catch (e: Exception) {
            Log.e("HomeScreen", "Erro ao obter nome do usuário", e)
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            NavBar(
                navController = navController,
                modifier      = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)                // desloca abaixo da NavBar
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color    = MaterialTheme.colorScheme.primary
                    )
                }
                nomeUsuario != null -> {
                    Column (modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card ( modifier = Modifier.fillMaxWidth()
                                .padding(8.dp)){
                                Box(modifier = Modifier.padding(16.dp)
                                    .fillMaxWidth(),
                                    contentAlignment = Alignment.Center,
                                ){
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Image(
                                            imageVector = Icons.Default.AccountCircle,
                                            contentDescription = "Adicionar Conta",
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text("Adicionar Conta")
                                    }
                            }
                        }
                        Card ( modifier = Modifier.fillMaxWidth()
                            .padding(8.dp)){
                            Box(modifier = Modifier.padding(16.dp)
                                .fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ){
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Image(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar Conta",
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("Editar Conta")
                                }
                            }
                        }
                        Card ( modifier = Modifier.fillMaxWidth()
                            .padding(8.dp)){
                            Box(modifier = Modifier.padding(16.dp)
                                .fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ){
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Image(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Deletar Conta",
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("Deletar Conta")
                                }
                            }
                        }
                    }
                }
                else -> {
                    Text(
                        text      = "Não foi possível carregar seu nome.Tente novamente",
                        modifier  = Modifier.align(Alignment.Center),
                        color     = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}