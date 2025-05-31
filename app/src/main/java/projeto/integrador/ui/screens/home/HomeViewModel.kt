package projeto.integrador.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import projeto.integrador.data.model.Access
import projeto.integrador.utilities.getAllAccess

class HomeViewModel : ViewModel() {

    private val _accessItems = MutableStateFlow<List<Access>>(emptyList())
    val accessItems: StateFlow<List<Access>> = _accessItems

    private val handler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    fun loadAccessItems() {
        viewModelScope.launch(handler) {
            val snapshots: List<DocumentSnapshot> = getAllAccess()
            val items = snapshots.mapNotNull { doc ->
                try {
                    doc.toObject(Access::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            _accessItems.value = items
        }
    }
}
