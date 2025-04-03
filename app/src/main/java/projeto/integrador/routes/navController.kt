package projeto.integrador.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import projeto.integrador.data.model.Usuario
import projeto.integrador.ui.screens.SignUpScreen
import projeto.integrador.ui.screens.singInScreen

//import projeto.integrador.ui.screens.SignInScreen

@Composable
fun NavigationSetup(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "signUp") {
        // Registro de usuários e tela inicial de usuários novos
        composable("signUp") {
            SignUpScreen(navController, Usuario("","","",""))
        }
        // Tela de login de usuários existentes
        composable("singIn"){
            singInScreen(navController, Usuario("","","",""))
        }
    }
}
