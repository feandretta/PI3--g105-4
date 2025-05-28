// Declaração do pacote
package projeto.integrador.ui.screens

// Imports do Jetpack Compose para UI
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Navegação
import androidx.navigation.NavHostController

// Corrotinas
import kotlinx.coroutines.launch

// Modelos e utilitários locais
import projeto.integrador.data.model.Usuario
import projeto.integrador.utilities.funcs.Cadastro

/**
 * Função auxiliar para controlar a visibilidade da senha.
 * 
 * @param visualTransformation Indica se a transformação visual está ativa
 * @return Boolean indicando se a senha deve ser visível
 */
fun visualizacaoSenha(visualTransformation: Boolean): Boolean {
    return !visualTransformation
}

/**
 * Tela de cadastro de novos usuários.
 * Permite que os usuários criem uma nova conta fornecendo nome, email e senha.
 *
 * @param navController Controlador de navegação para gerenciar a navegação entre telas
 * @param usuario Objeto Usuario para armazenar os dados do novo usuário
 * @param modifier Modificador para personalizar o layout da tela
 */
@Composable
fun SignUpScreen(
    navController: NavHostController, 
    usuario: Usuario, 
    modifier: Modifier = Modifier.fillMaxSize()
) {
    // Estados para os campos do formulário
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    
    // Estados para controle da UI
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var senhaVisivel by remember { mutableStateOf(false) }

    // Estados para o diálogo de confirmação
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    // Diálogo de confirmação que é exibido após tentativa de cadastro
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Alerta") },
            text = { Text(text = dialogMessage) },
            confirmButton = {
                Button(
                    onClick = { 
                        showDialog = false 
                        // Se o cadastro foi bem-sucedido, navega para a tela de login
                        if (dialogMessage.contains("sucesso")) {
                            navController.navigate("signIn")
                        }
                    }, 
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Layout principal da tela
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Cabeçalho da tela
        Text("Olá, seja bem vindo!", fontSize = 24.sp)
        Text("Crie sua conta", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para o nome completo
        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Campo para o email
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Campo para a senha (com opção de mostrar/ocultar)
        TextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Campo para confirmar a senha
        TextField(
            value = confirmarSenha,
            onValueChange = { confirmarSenha = it },
            label = { Text("Confirmar Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation()
        )

        // Botão para alternar a visibilidade da senha
        TextButton(
            onClick = { senhaVisivel = !senhaVisivel }
        ) {
            Text(if (senhaVisivel) "Ocultar Senha" else "Mostrar Senha")
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Linha com botões de ação
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Link para a tela de login
            TextButton(
                onClick = { navController.navigate("signIn") }
            ) {
                Text("Já tenho uma conta", textDecoration = TextDecoration.Underline)
            }
            
            // Botão para realizar o cadastro
            Button(
                onClick = {
                    // Executa o cadastro em uma corrotina para não travar a UI
                    scope.launch {
                        // Chama a função de cadastro passando os dados fornecidos
                        val resultado = Cadastro(context, nome, email, senha, confirmarSenha)
                        
                        // Define a mensagem de acordo com o resultado do cadastro
                        dialogMessage = if (resultado) {
                            "Cadastro realizado com sucesso!"
                        } else {
                            "Erro ao realizar o cadastro! Verifique os dados e tente novamente."
                        }
                        
                        // Exibe o diálogo com a mensagem
                        showDialog = true
                    }
                }
            ) {
                Text("Cadastrar")
            }
        }
    }
}