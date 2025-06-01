package projeto.integrador.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ProfileScreen() {
    val auth = Firebase.auth
    val db = Firebase.firestore

    val user = auth.currentUser
    val uid = user?.uid ?: "uid"
    var nomeUsuario by remember { mutableStateOf("") }
    var emailUsuario by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var updateStatus by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Busca as informações do usuário ao iniciar a tela
    LaunchedEffect(Unit) {
        try {
            val document = db.collection("usuarios")
                .document(uid)
                .get()
                .await()
            nomeUsuario = document.getString("nome") ?: ""
            // Se o email não estiver salvo no Firestore, pega do Firebase Auth
            emailUsuario = document.getString("email") ?: (user?.email ?: "")
        } catch (e: Exception) {
            Log.e("ProfileScreen", "Erro ao obter os dados do usuário", e)
        } finally {
            isLoading = false
        }
    }

    Scaffold(){
        innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {

                // Campo para editar o nome
                OutlinedTextField(
                    value = nomeUsuario,
                    onValueChange = { nomeUsuario = it },
                    label = { Text("Nome") },
                    colors = OutlinedTextFieldDefaults.colors(
                    )
                )
                Spacer(modifier = Modifier.padding(8.dp))
                // Campo para editar o email
                OutlinedTextField(
                    value = emailUsuario,
                    onValueChange = { emailUsuario = it },
                    label = { Text("Email") }
                )
                // Botão para salvar as alterações
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                // Atualiza os dados no Firestore
                                db.collection("usuarios")
                                    .document(uid)
                                    .update(
                                        mapOf(
                                            "nome" to nomeUsuario,
                                            "email" to emailUsuario
                                        )
                                    )
                                    .await()

                                // Atualiza os dados no Firebase Auth
                                user?.let {
                                    // Atualiza o email do usuário
                                    it.verifyBeforeUpdateEmail(emailUsuario).await()

                                    // Atualiza o nome (displayName) do usuário
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(nomeUsuario)
                                        .build()
                                    it.updateProfile(profileUpdates).await()
                                }

                                updateStatus = "Dados atualizados com sucesso!"
                            } catch (e: Exception) {
                                Log.e("ProfileScreen", "Erro ao atualizar os dados do usuário", e)
                                updateStatus = "Falha ao atualizar os dados."
                            }
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Salvar")
                }
                // Exibe uma mensagem de confirmação ou erro
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
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}