package projeto.integrador.utilities.funcs


import android.content.Context
import androidx.annotation.RequiresPermission
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import projeto.integrador.data.model.User

// Função suspensa para criar usuário e salvar os dados no Firestore
@RequiresPermission("android.permission.READ_PHONE_STATE")
suspend fun Cadastro(
    context: Context,
    usuario: User
): Boolean {
    if (usuario.isEmpty()) return false
    usuario.senha?.length?.let { if (it < 6) return false }

    val auth = Firebase.auth
    val db = Firebase.firestore

    return try {
        val authResult = auth.createUserWithEmailAndPassword(usuario.email.toString(),
                                                             usuario.senha.toString())
                                                             .await()
        usuario.uid = authResult.user?.uid ?: return false
        usuario.imei = DeviceUtils.getDeviceImei(context)

        db.collection("usuarios")
            .document(usuario.uid.toString())
            .set(usuario)
            .await()

        true
    } catch (e: Exception) {

        e.printStackTrace()

        Log.e("Cadastro", "Erro ao cadastrar usuário", e)

        false
    }
}
//ela só existe para não haver chamadas de firebase no front da aplicação
fun signOut(){
    val auth = Firebase.auth

    auth.signOut()
}
