package projeto.integrador.utilities.funcs

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

data class Category(
    val id: String = "",
    val name: String = "",
    val userId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

suspend fun filterCategories(): List<Category> {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    return try {
        if (currentUser == null) {
            emptyList()
        } else {
            val categoriesSnapshot = db.collection("categories")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .await()

            categoriesSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Category::class.java)
            }
        }
    } catch (e: Exception) {
        emptyList()
    }
}

suspend fun createCategory(name: String): Boolean {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    return try {
        if (currentUser == null) {
            false
        } else {
            val category = Category(
                name = name,
                userId = currentUser.uid
            )

            db.collection("categories")
                .add(category)
                .await()

            true
        }
    } catch (e: Exception) {
        false
    }
}