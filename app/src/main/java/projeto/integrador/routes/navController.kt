package projeto.integrador.routes

import HomeScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import projeto.integrador.data.model.Usuario
import projeto.integrador.ui.screens.SignUpScreen
import projeto.integrador.ui.screens.signInScreen

//import projeto.integrador.ui.screens.SignInScreen

@Composable
fun NavigationSetup(navController: NavHostController) {

    val isLogged = FirebaseAuth.getInstance().currentUser != null

    val startDestination = if (isLogged) "home" else "signUp"

    NavHost(navController = navController, startDestination = startDestination) {
        // Registro de usuários e tela inicial de usuários novos
        composable("signUp") {
            SignUpScreen(navController, Usuario("","","",""))
        }
        // Tela de login de usuários existentes
        composable("signIn"){
            val context = LocalContext.current
            signInScreen(
                context = context,
                modifier = Modifier,
                usuario = Usuario("","","",""),
                navHostController = navController
            )
        }
        composable("home"){
            HomeScreen(modifier = Modifier,navController)
        }
    }
}
