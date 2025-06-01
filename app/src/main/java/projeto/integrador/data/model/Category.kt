package projeto.integrador.data.model

import com.google.firebase.Timestamp

data class Category(
    var name: String = "",
    var createdAt: Timestamp = Timestamp.now()
)