package projeto.integrador.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.auth.User
import androidx.compose.ui.Modifier
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import projeto.integrador.data.funcs.Cadastro
import projeto.integrador.data.model.Usuario

@Composable
fun SignUpScreen(navController: NavHostController, usuario: Usuario) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Estado para controlar a exibição do diálogo de confirmação
    var showDialog by remember { mutableStateOf(false) }
    // Estado para a mensagem de confirmação
    var dialogMessage by remember { mutableStateOf("") }

    // Exibe o AlertDialog se showDialog for true
    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirmação") },
            text = { Text(text = dialogMessage) },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Olá, seja bem vindo!", fontSize = 24.sp)
        Text("Crie sua conta", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = confirmarSenha,
            onValueChange = { confirmarSenha = it },
            label = { Text("Confirmar Senha") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.navigate("signIn") }) {
                Text("Já tenho uma conta", textDecoration = TextDecoration.Underline)
            }
            Button(onClick = {
                // Chama a função de cadastro e exibe o pop-up de confirmação
                val resultado = Cadastro(nome, email, senha, confirmarSenha)
                if (resultado) {
                    dialogMessage = "Cadastro realizado com sucesso!"
                } else {
                    dialogMessage = "Erro ao realizar o cadastro!"
                }
                showDialog = true
            }) {
                Text("Cadastrar")
            }
        }
    }
}