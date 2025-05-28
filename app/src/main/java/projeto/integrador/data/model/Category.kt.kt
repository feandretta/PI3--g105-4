package projeto.integrador.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

/**
 * Classe que representa uma categoria de despesa/receita do usuário
 */
data class Category(
    @DocumentId
    val id: String = "",
    var name: String = "",
    var color: String = "#6200EE", // Cor padrão
    var userId: String = "",
    var createdAt: com.google.firebase.Timestamp = Timestamp.now()
) {
    // Construtor vazio necessário para o Firestore
    constructor() : this("", "", "#6200EE", "", Timestamp.now())
    
    // Mapeamento de campos para o Firestore
    companion object {
        const val FIELD_NAME = "name"
        const val FIELD_COLOR = "color"
        const val FIELD_USER_ID = "userId"
        const val FIELD_CREATED_AT = "createdAt"
    }
}