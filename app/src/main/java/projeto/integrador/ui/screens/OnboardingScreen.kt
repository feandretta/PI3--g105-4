package projeto.integrador.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.core.content.edit

@Composable
fun OnboardingScreen(
    context: Context,
    navHostController: NavHostController
) {
    val sharedPreferences = context.getSharedPreferences(
        "projeto.integrador.PREFERENCE_FILE_KEY",
        Context.MODE_PRIVATE
    )

    val isFirstTime = sharedPreferences.getBoolean("isFirstTime",true)

    if (isFirstTime){
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
    } else{
        navHostController.navigate("signUp")
    }

}