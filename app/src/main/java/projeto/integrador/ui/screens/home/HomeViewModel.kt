package projeto.integrador.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import projeto.integrador.data.model.Access
import projeto.integrador.utilities.funcs.getAccessByUser

class HomeViewModel : ViewModel() {

    // StateFlow contendo a lista de Access
    private val _accessItems = MutableStateFlow<List<Access>>(emptyList())
    val accessItems: StateFlow<List<Access>> = _accessItems

    private val handler = CoroutineExceptionHandler { _, exception ->
        // Tratar ou logar erro
        exception.printStackTrace()
    }

    init {
        loadAccessItems()
    }

    private fun loadAccessItems() {
        viewModelScope.launch(handler) {
            // Busca DocumentSnapshot do Firebase
            val snapshots: List<DocumentSnapshot> = getAccessByUser()

            // Converte cada DocumentSnapshot em Access
            val items = snapshots.mapNotNull { doc ->
                try {
                    // Usa toObject para mapear campos automaticamente
                    doc.toObject(Access::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            // Atualiza o StateFlow
            _accessItems.value = items
        }
    }
}
