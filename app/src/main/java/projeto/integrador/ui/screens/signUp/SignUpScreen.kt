package projeto.integrador.ui.screens.signUp

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import projeto.integrador.R

@Composable
fun SignUpScreen(
    context: Context,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: SignUpViewModel = remember { SignUpViewModel() }
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )

            Text(
                text = "Super ID",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                state = viewModel.nomeState,
                label = { Text("Nome") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            OutlinedTextField(
                state = viewModel.emailState,
                label = { Text("Endereço de E-mail") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            OutlinedSecureTextField(
                state = viewModel.senhaState,
                label = { Text("Senha") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textObfuscationMode =
                    if (viewModel.senhaVisivel.value)
                        TextObfuscationMode.Visible
                    else
                        TextObfuscationMode.RevealLastTyped,
                trailingIcon = {
                    val icon =
                        if (viewModel.senhaVisivel.value)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff

                    IconButton(onClick = { viewModel.toggleSenhaVisivel() }) {
                        Icon(icon, contentDescription = null)
                    }
                }
            )

            OutlinedSecureTextField(
                state = viewModel.confirmarSenhaState,
                label = { Text("Confirmar Senha") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textObfuscationMode =
                    if (viewModel.confirmarSenhaVisivel.value)
                        TextObfuscationMode.Visible
                    else
                        TextObfuscationMode.RevealLastTyped,
                trailingIcon = {
                    val icon =
                        if (viewModel.confirmarSenhaVisivel.value)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff

                    IconButton(onClick = { viewModel.toggleConfirmarSenhaVisivel() }) {
                        Icon(icon, contentDescription = null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))



            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = {
                    viewModel.signUp(context) { success, message ->
                        if (success) navController.navigate("home")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Criar conta",
                    style = MaterialTheme.typography.bodyLarge
                    )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Já tem uma conta? ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "Faça login",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.clickable {
                        navController.navigate("signIn")
                    }
                )
            }
        }
    }
}
