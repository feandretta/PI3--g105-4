package projeto.integrador.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import projeto.integrador.data.model.Access
import projeto.integrador.utilities.funcs.validation


@Composable
fun AccessAddScreen(modifier: Modifier = Modifier.fillMaxSize(), navController: NavHostController) {
    val db = Firebase.firestore
    val auth = Firebase.auth


    val uid = auth.currentUser?.uid ?: "uid"

    var categoria by remember { mutableStateOf("")}
    var senha by remember { mutableStateOf("")}
    var dominio by remember { mutableStateOf("")}
    var email by remember { mutableStateOf("")}
    var descricao by remember { mutableStateOf("")}
    var isVisible = true
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Adicionar accesso")

        //Coluna de organização
        Column {
            Text("Categoria")
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false}) {
                DropdownMenuItem(text = { Text("SiteWeb")}, onClick = { categoria = "SiteWeb"; isVisible = true})
                DropdownMenuItem(text = { Text("Aplicativo")}, onClick = { categoria = "Aplicativo"})
                DropdownMenuItem(text = { Text("Teclado Físico")}, onClick = { categoria = "Teclado Físico"})

            }
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            if(isVisible){
                TextField(
                    value = dominio,
                    onValueChange = { dominio = it },
                    label = { Text("Dominio") },
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

        }
    }



}