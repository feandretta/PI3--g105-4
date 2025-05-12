package projeto.integrador.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun OnboardingScreen(
    context: Context,
    navHostController: NavHostController
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Bem vindo")
            Button(onClick = {navHostController.navigate("signUp")}) {
                Text("Continuar")
            }
        }
    }
}