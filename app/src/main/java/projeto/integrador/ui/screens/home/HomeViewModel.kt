package projeto.integrador.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import projeto.integrador.data.model.Access
import projeto.integrador.utilities.deleteAccess
import projeto.integrador.utilities.getAllAccess

data class AccessWithId(
    val id: String,
    val access: Access
)

class HomeViewModel : ViewModel() {

    private val _accessItems = MutableStateFlow<List<AccessWithId>>(emptyList())
    val accessItems: StateFlow<List<AccessWithId>> = _accessItems

    private val handler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    fun loadAccessItems() {
        viewModelScope.launch(handler) {
            val snapshots: List<DocumentSnapshot> = getAllAccess()
            val items = snapshots.mapNotNull { doc ->
                try {
                    val access = doc.toObject(Access::class.java)
                    if (access != null) {
                        AccessWithId(doc.id, access)
                    } else null
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            _accessItems.value = items
        }
    }

    fun deleteAccessById(id: String, onComplete: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            try {
                val success = deleteAccess(id)
                if (success) {
                    loadAccessItems()
                    onComplete()
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

}
