// Declaração do pacote
package projeto.integrador.ui.screens

// Imports do Android
import android.content.Context
import android.widget.Toast

// Imports do Jetpack Compose para UI
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Navegação
import androidx.navigation.NavHostController

// Modelos e utilitários locais
import projeto.integrador.data.model.Usuario
import projeto.integrador.utilities.funcs.validation

/**
 * Tela de login do aplicativo.
 * Permite que os usuários façam login com email e senha.
 *
 * @param context Contexto da aplicação Android
 * @param modifier Modificador para personalizar o layout
 * @param usuario Objeto Usuario para armazenar os dados do usuário logado
 * @param navHostController Controlador de navegação para gerenciar a navegação entre telas
 */
@Composable
fun signInScreen(
    context: Context,
    modifier: Modifier,
    usuario: Usuario,
    navHostController: NavHostController
) {
    // Estados locais para os campos de entrada
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    // Layout principal da tela
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Cabeçalho da tela
        Text("Olá, seja bem vindo!", fontSize = 24.sp)
        Text("Login")

        // Formulário de login
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            // Campo de email
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.padding(top = 16.dp)
            )
            
            // Campo de senha (com máscara)
            TextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(), // Esconde o texto da senha
                modifier = Modifier.padding(top = 16.dp)
            )
            
            // Botão de login
            Column (modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Button(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .width(109.dp),
                    onClick = {
                        // Valida as credenciais do usuário
                        validation(context, email, senha) { success, message ->
                            // Mostra mensagem de feedback para o usuário
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                            // Se o login for bem-sucedido, navega para a tela inicial
                            if (success) {
                                println("Login realizado com sucesso")
                                navHostController.navigate("home")
                            }
                        }
                    }
                ) {
                    Text("Entrar")
                }

                // Link para a tela de cadastro
                Button(
                    onClick = {
                        // Navega para a tela de cadastro
                        navHostController.navigate("signUp")
                    }
                ) {
                    Text("Cadastrar", textDecoration = TextDecoration.Underline)
                }
            }
        }
    }
}