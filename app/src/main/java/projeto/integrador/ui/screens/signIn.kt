package projeto.integrador.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import projeto.integrador.utilities.funcs.validation
import projeto.integrador.data.model.Usuario
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import projeto.integrador.routes.NavigationSetup


@Composable
fun signInScreen(context:Context,modifier: Modifier, usuario: Usuario, navHostController: NavHostController){

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
        ) {

        //Titulo
        Text("Olá, seja bem vindo!", fontSize = 24.sp)
        Text("Login")

        //Coluna de organização
        Column {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.padding(top = 16.dp)
            )
            TextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(top = 16.dp)
            )
            Button(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp),onClick = {
                validation(context, email, senha) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        println("Sucesso")
                        navHostController.navigate("home")
                    }
                }
            }) {
                Text("Entrar")
            }
        }
    }
}