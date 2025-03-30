package projeto.integrador.data.funcs

import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import projeto.integrador.data.model.Usuario

fun Cadastro(nome: String, email: String, senha: String, confirmarSenha: String): Boolean{

    val auth = Firebase.auth
    val db = Firebase.firestore

    auth.createUserWithEmailAndPassword(email, senha)

    db.collection("usuarios")
        .add(Usuario(nome, email, senha, confirmarSenha))
        .addOnSuccessListener {

        }
        .addOnFailureListener {

        }
    return true
}