package projeto.integrador.utilities.funcs

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import projeto.integrador.data.model.Access


suspend fun AccessRegister(access: Access): Boolean{
    if(access.AccessIsEmpty()){
        val auth = Firebase.auth
        val db = Firebase.firestore
        val uid = auth.currentUser?.uid ?: "uid"

        return try{
            db.collection("usuarios").document(uid).collection("acessos").add(access).await()

            true
        }catch (e: Exception) {

            e.printStackTrace()

            Log.e("Registro Acesso", "Erro ao registrar acesso", e)

            false
        }

    }
    return false
}