package projeto.integrador.utilities


import android.content.Context
import androidx.annotation.RequiresPermission
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import projeto.integrador.data.model.User

/**
 * Cria categorias pré-definidas para um novo usuário
 * @param userId ID do usuário para o qual as categorias serão criadas
 */
private suspend fun createDefaultCategories(userId: String) {
    val db = Firebase.firestore
    val categories = listOf(
        hashMapOf(
            "name" to "Sites Web",
            "createdAt" to Timestamp.now()
        ),
        hashMapOf(
            "name" to "Aplicativos",
            "createdAt" to Timestamp.now()
        ),
        hashMapOf(
            "name" to "Acesso Fisico",
            "createdAt" to Timestamp.now()
        )
    )

    // Adiciona cada categoria ao Firestore
    categories.forEach { category ->
        try {
            db.collection("usuarios")
                .document(userId)
                .collection("categories")
                .add(category)
                .await()
        } catch (e: Exception) {
            Log.e("Cadastro", "Erro ao criar categoria padrão: ${e.message}")
        }
    }
}

/**
 * Função suspensa para criar usuário e salvar os dados no Firestore
 * @param context Contexto da aplicação
 * @param nome Nome do usuário
 * @param email Email do usuário
 * @param senha Senha do usuário
 * @param confirmarSenha Confirmação da senha
 * @return Boolean indicando se o cadastro foi bem-sucedido
 */
@RequiresPermission("android.permission.READ_PHONE_STATE")
suspend fun cadastro(
    context: Context,
    usuario: User
): Boolean {

    if (usuario.nome.isNullOrBlank() || usuario.email.isNullOrBlank() || usuario.senha.isNullOrBlank()){
        return false
    }
    
    usuario.senha?.length?.let { if (it < 6) return false }

    val auth = Firebase.auth
    val db = Firebase.firestore

    return try {
        val authResult = auth.createUserWithEmailAndPassword(usuario.email.toString(),
                                                             usuario.senha.toString())
                                                             .await()
        usuario.uid = authResult.user?.uid ?: return false
        usuario.imei = DeviceUtils.getDeviceImei(context)

        //Manda o email pra verificação
        authResult.user?.sendEmailVerification()

        // Salva os dados do usuário no Firestore
        db.collection("usuarios")
            .document(usuario.uid.toString())
            .set(usuario)
            .await()

        // Cria as categorias pré-definidas para o novo usuário
        createDefaultCategories(usuario.uid.toString())

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
