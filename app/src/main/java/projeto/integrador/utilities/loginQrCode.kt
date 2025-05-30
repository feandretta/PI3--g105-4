package projeto.integrador.utilities

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

fun loginQrCode(loginToken: String, onResult: (Boolean, Exception?) -> Unit) {
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
            Log.d("Login QR Code", "$result")
            onResult(true, null)
        }
        .addOnFailureListener { exception ->
            Log.d("Login QR Code", "Error updating document: ", exception)
            onResult(false, exception)
        }
}
