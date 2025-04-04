package projeto.integrador.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import projeto.integrador.data.model.Usuario

@Composable
fun homeScreen(navController: NavHostController){

    val auth = Firebase.auth
    val usuario = auth.currentUser?.email

    Column(){
        Text("Olá, seja bem vindo ${usuario}!", fontSize = 24.sp)
    }
}