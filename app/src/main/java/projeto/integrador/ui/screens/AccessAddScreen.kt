package projeto.integrador.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


@Composable
fun AccessAddScreen(modifier: Modifier = Modifier.fillMaxSize(), navController: NavHostController) {
    val db = Firebase.firestore
    val auth = Firebase.auth


    val uid = auth.currentUser?.uid ?: "uid"

    var nomeUsuario by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val document = db.collection("usuarios")
                .document(uid)
                .get()
                .await()
            nomeUsuario = document.getString("nome") ?: ""
        } catch (e: Exception) {
            Log.e("HomeScreen", "Erro ao obter o nome do usuário", e)
        } finally {
            isLoading = false
        }
    }



}