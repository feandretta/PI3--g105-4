package projeto.integrador.routes

import projeto.integrador.ui.screens.home.HomeScreen
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
import projeto.integrador.ui.screens.CategoriasScreen
import projeto.integrador.ui.screens.OnboardingScreen
import projeto.integrador.ui.screens.PasswordRecoveryScreen
import projeto.integrador.ui.screens.ProfileScreen
import projeto.integrador.ui.screens.signIn.SignInScreen
import projeto.integrador.ui.screens.signUp.SignUpScreen
import projeto.integrador.ui.screens.unlock.UnlockScreen


//import projeto.integrador.ui.screens.SignInScreen

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
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
        isLogged -> "unlock"
        isFirstTime -> "onboarding"
        else -> "signUp"
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // Tela de onboarding
        composable("onboarding"){
            OnboardingScreen(
                navController = navController,
                sharedPreferences = sharedPreferences
            )
        }

        // Registro de usuários e tela inicial de usuários novos
        composable("signUp") {
            SignUpScreen(
                context = context,
                modifier = Modifier,
                navController = navController
            )
        }
        // Tela de login de usuários existentes
        composable("signIn"){
            SignInScreen(
                context = context,
                modifier = Modifier,
                navController = navController
            )
        }
        composable("home"){
            HomeScreen(
                modifier = Modifier,
                navController = navController
            )
        }
        composable("categorias") {
            CategoriasScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen()
        }

        composable("forgotPassword"){
            PasswordRecoveryScreen(
                navController = navController,
                context = context
            )
        }

        composable("unlock") {
            UnlockScreen(
                context = context,
                navController = navController,
            )
        }
    }
}
