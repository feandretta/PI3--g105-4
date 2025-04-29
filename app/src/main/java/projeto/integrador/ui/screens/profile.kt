package projeto.integrador.ui.screens

import NavBar
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.google.firebase.auth.EmailAuthProvider


@Composable
fun ProfileScreen(navController: NavHostController) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val user = auth.currentUser
    val uid = user?.uid ?: return

    var nomeUsuario by remember { mutableStateOf("") }
    var emailUsuario by remember { mutableStateOf("") }
    var senhaAtual by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var updateStatus by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Buscar dados do Firestore
    LaunchedEffect(Unit) {
        try {
            val document = db.collection("usuarios").document(uid).get().await()
            nomeUsuario = document.getString("nome") ?: ""
            emailUsuario = document.getString("email") ?: user?.email.orEmpty()
        } catch (e: Exception) {
            Log.e("ProfileScreen", "Erro ao obter dados", e)
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp)
            .safeContentPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            NavBar(navController)

            TextField(
                value = nomeUsuario,
                onValueChange = { nomeUsuario = it },
                label = { Text("Nome") }
            )

            TextField(
                value = emailUsuario,
                onValueChange = { emailUsuario = it },
                label = { Text("Email") }
            )

            TextField(
                value = senhaAtual,
                onValueChange = { senhaAtual = it },
                label = { Text("Senha Atual") },
                visualTransformation = PasswordVisualTransformation()
            )

            Button(
                onClick = {
                    if (senhaAtual.isBlank()) {
                        updateStatus = "Digite sua senha atual para atualizar o email."
                        return@Button
                    }

                    scope.launch {
                        try {
                            updateStatus = "Reautenticando..."
                            val credential = EmailAuthProvider.getCredential(user?.email ?: "", senhaAtual)
                            user?.reauthenticate(credential)?.await()

                            updateStatus = "Atualizando email..."
                            user?.updateEmail(emailUsuario)?.await()

                            updateStatus = "Atualizando nome..."
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(nomeUsuario)
                                .build()
                            user?.updateProfile(profileUpdates)?.await()

                            // Forçar recarregamento dos dados do Firebase Auth
                            auth.currentUser?.reload()

                            updateStatus = "Salvando no Firestore..."
                            db.collection("usuarios").document(uid).update(
                                mapOf(
                                    "nome" to nomeUsuario,
                                    "email" to emailUsuario
                                )
                            ).await()

                            updateStatus = "Dados atualizados com sucesso!"
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Erro ao atualizar", e)
                            updateStatus = "Erro: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Salvar")
            }

            if (updateStatus.isNotEmpty()) {
                Text(
                    text = updateStatus,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
