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
        composable("signUp") {
            SignUpScreen(navController, Usuario("","","",""))
        }
        composable("singIn"){
            singInScreen(navController, Usuario("","","",""))
        }
    }
}
