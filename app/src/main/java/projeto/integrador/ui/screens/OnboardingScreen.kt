package projeto.integrador.ui.screens

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.core.content.edit

@SuppressLint("CommitPrefEdits")
@Composable
fun OnboardingScreen(navHostController: NavHostController, sharedPreferences: SharedPreferences) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Bem vindo")
            Button(onClick = {
                sharedPreferences.edit() { putBoolean("isFirstTime", false) }
                navHostController.navigate("signUp")
            }) {
                Text("Continuar")
            }
        }

    }
}