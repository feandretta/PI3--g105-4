package projeto.integrador.utilities.funcs

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import projeto.integrador.config.generateAccessToken
import projeto.integrador.data.model.Access
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//essas chamadas de db e auth são horrorosas dps vou criar um função de repositorio pra isso
//função para cadastrar as senhas
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
suspend fun accessRegister(access: Access): Boolean{
    //ele não irá retornar caso a SENHA, CATEGORIA ou NOME sejam vazios os demais campos são opcionais
    if(!access.AccessIsEmpty()){
        val auth = Firebase.auth
        val db = Firebase.firestore
        val uid = auth.currentUser?.uid ?: "uid"

        access.senha = CryptoUtils.encrypt(access.senha.toString())

        return try{
            db.collection("usuarios")
                .document(uid)
                .collection("acessos")
                .add(access).await()

            true
        }catch (e: Exception) {

            e.printStackTrace()

            Log.e("Registro Acesso", "Erro ao registrar acesso", e)

            false
        }

    }
    return false
}
//retorna todos os acessos do usuário como uma lista de objetos do tipo documentSnapshot
//DOCUMENT SNAPSHOT cotem o .ID do documento no firebase e .getData() pras informações de fato
suspend fun getAccessByUser(): List<DocumentSnapshot> {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val uid = auth.currentUser?.uid ?: "uid"

    return suspendCoroutine { continuation ->
        db.collection("usuarios")
            .document(uid)
            .collection("acessos")
            .get()
            .addOnSuccessListener { result ->
                val documentSnapshots = mutableListOf<DocumentSnapshot>()
                for (document in result) {
                    Log.d("Get Acessos", "${document.id} => ${document.data}")
                    documentSnapshots.add(document)
                }
                continuation.resume(documentSnapshots)
            }
            .addOnFailureListener { exception ->
                Log.d("Get Acessos", "Error getting documents: ", exception)
                continuation.resumeWithException(exception)
            }
    }
}
//mesma coisa que um get normal mas esse você passa o id do acesso específico que você quer retornar
// EM STRING PEDRÃO
fun getAccessByUser(idAccess : String){
    val auth = Firebase.auth
    val db = Firebase.firestore
    val uid = auth.currentUser?.uid ?: "uid"


    val docRef = db.collection("usuarios").document(uid).collection("accessos").document(idAccess)
    docRef.get()
        .addOnSuccessListener { document ->
            if (document != null) {
                Log.d("Get Access", "DocumentSnapshot data: ${document.data}")

            } else {
                Log.d("Get Access", "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d("Get Access", "get failed with ", exception)
        }
}
//passa o id e o acesso novo que ele edita
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
suspend fun alterAccess(idAccess: String, accessAtualizado: Access): Boolean{
    val auth = Firebase.auth
    val db = Firebase.firestore
    val uid = auth.currentUser?.uid ?: "uid"

    accessAtualizado.accessToken = generateAccessToken()
    accessAtualizado.senha = CryptoUtils.encrypt(accessAtualizado.senha.toString())

    val docRef = db.collection("usuarios").document(uid).collection("acessos").document(idAccess)

    return suspendCoroutine { continuation ->
        docRef.update(mapOf("nome" to accessAtualizado.nome,
                            "categoria" to accessAtualizado.categoria,
                            "parceiro" to accessAtualizado.dominio,
                            "email" to accessAtualizado.email,
                            "senha" to accessAtualizado.senha,
                            "descricao" to accessAtualizado.descricao))
            .addOnSuccessListener { result ->
                Log.d("Update Acesso", "$result")
                continuation.resume(true)
            }
            .addOnFailureListener { exception ->
                Log.d("Update Acesso", "Error updating document: ", exception)
                continuation.resumeWithException(exception)
            }
    }

    return false
}

//passa o id do acesso e ele deleta permanentemente
suspend fun deleteAccess(idAccess: String): Boolean{
    val auth = Firebase.auth
    val db = Firebase.firestore
    val uid = auth.currentUser?.uid ?: "uid"

    val docRef = db.collection("usuarios").document(uid).collection("acessos").document(idAccess)

    return suspendCoroutine { continuation ->
        docRef.delete()
            .addOnSuccessListener { result ->
                Log.d("Delete Acesso", "$result")
                continuation.resume(true)
            }
            .addOnFailureListener { exception ->
                Log.d("Delete Acesso", "Error updating document: ", exception)
                continuation.resumeWithException(exception)
            }
    }

    return false
}

