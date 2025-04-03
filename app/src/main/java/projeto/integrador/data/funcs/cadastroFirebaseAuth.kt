package projeto.integrador.data.funcs

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await



// Função suspend que cria o usuário, obtém o UID e salva os dados no Firestore
suspend fun Cadastro(
    context: Context,
    nome: String,
    email: String,
    senha: String,
    confirmarSenha: String
): Boolean {
    // Valida os campos (você pode personalizar o tratamento de erro se desejar)
    if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) return false
    if (senha != confirmarSenha) return false

    val auth = Firebase.auth
    val db = Firebase.firestore

    return try {
        // Cria o usuário no Firebase Authentication e aguarda o resultado
        val authResult = auth.createUserWithEmailAndPassword(email, senha).await()
        val uid = authResult.user?.uid ?: return false

        // Obtém o IMEI do dispositivo (pode ser nulo se não obtiver ou se não tiver permissão)

        // Prepara os dados para salvar no Firestore; aqui, usamos o UID como identificador do documento
        val userMap = hashMapOf(
            "nome" to nome,
            "email" to email,
            "uid" to uid,
        )

        db.collection("usuarios")
            .document(uid)
            .set(userMap)
            .await()

        true
    } catch (e: Exception) {
        // Aqui você pode logar o erro para depuração, se necessário
        false
    }
}
