package projeto.integrador.utilities

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

fun isValidFirestoreDocumentId(id: String): Boolean {
    return id.isNotBlank() && !id.contains("/") && id.length <= 1500
}

fun loginQrCode(loginToken: String, onResult: (String) -> Unit) {

    if (!isValidFirestoreDocumentId(loginToken)) {
        onResult("QR Code inválido")
        return
    }

    try {
        val auth = Firebase.auth
        val db = Firebase.firestore
        val uid = auth.currentUser?.uid ?: "uid"

        val docRef = db.collection("login").document(loginToken)

        docRef.update(
            mapOf(
                "status" to "authenticated",
                "user" to uid,
                "authenticatedAt" to Timestamp.now()
            )
        )
            .addOnSuccessListener { result ->
                onResult("Login realizado com sucesso")
            }
            .addOnFailureListener { exception ->
                onResult("Falha ao autenticar o login, Tente novamente")
            }
    }catch (e: Exception) {
        onResult("QR Code inválido")
    }

}
