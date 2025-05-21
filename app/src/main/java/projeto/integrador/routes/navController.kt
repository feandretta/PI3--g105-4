package projeto.integrador.routes

import HomeScreen
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import projeto.integrador.data.model.Usuario
import projeto.integrador.ui.screens.OnboardingScreen
import projeto.integrador.ui.screens.ProfileScreen
import projeto.integrador.ui.screens.SignUpScreen
import projeto.integrador.ui.screens.signInScreen
import projeto.integrador.ui.screens.ConfigScreen

//import projeto.integrador.ui.screens.SignInScreen

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun NavigationSetup(navController: NavHostController) {

    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences(
        "projeto.integrador.PREFERENCE_FILE_KEY",
        Context.MODE_PRIVATE
    )

    val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)

    val isLogged = FirebaseAuth.getInstance().currentUser != null

    val startDestination = when {
        isLogged -> "home"
        isFirstTime -> "onboarding"
        else -> "signUp"
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // Tela de onboarding
        composable("onboarding"){
            OnboardingScreen(navHostController = navController,sharedPreferences)
        }

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
        composable("profile"){
            ProfileScreen(navController)
        }

        composable("settings"){
            ConfigScreen(modifier = Modifier,navController)
        }
    }
}
