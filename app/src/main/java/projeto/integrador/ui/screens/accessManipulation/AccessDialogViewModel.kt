package projeto.integrador.ui.screens.accessManipulation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import projeto.integrador.config.generateAccessToken
import projeto.integrador.data.model.Access
import projeto.integrador.utilities.CryptoManager
import projeto.integrador.utilities.alterAccess
import projeto.integrador.utilities.getAccessByUser
import projeto.integrador.utilities.getAllCategoryNames
import projeto.integrador.utilities.registerAccess

enum class AccessDialogMode {
    VIEW, EDIT, CREATE
}

class AccessDialogViewModel : ViewModel() {
    val nome = mutableStateOf("")
    val url = mutableStateOf("")
    val email = mutableStateOf("")
    val senha = mutableStateOf("")
    val descricao = mutableStateOf("")
    val categoria = mutableStateOf("Selecionar Categoria")

    val categoriasDisponiveis = mutableStateOf<List<String>>(emptyList())

    val mode = mutableStateOf(AccessDialogMode.CREATE)
    var accessId: String? = null

    fun loadAccessData(access: Access, id: String?, mode: AccessDialogMode) {
        this.mode.value = mode
        this.accessId = id
        nome.value = access.nome.orEmpty()
        url.value = access.dominio.orEmpty()
        email.value = access.email.orEmpty()
        descricao.value = access.descricao.orEmpty()
        categoria.value = access.categoria.orEmpty()

        if (access.senha.orEmpty() != ""){
            senha.value = CryptoManager.decrypt(access.senha.toString())
        }else{
            senha.value = access.senha.orEmpty()
        }
    }

    fun isFormValid(): Boolean {
        return nome.value.isNotBlank() && senha.value.isNotBlank()
    }

    fun loadAccessById(id: String, mode: AccessDialogMode) {
        viewModelScope.launch {
            try {
                val snapshot = getAccessByUser(id)
                val access = snapshot.toObject(Access::class.java)
                if (access != null) {
                    loadAccessData(access, id, mode)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun salvar(onComplete: () -> Unit) {
        viewModelScope.launch {
            val access = Access(
                nome = nome.value,
                categoria = categoria.value,
                dominio = url.value,
                email = email.value,
                senha = senha.value,
                descricao = descricao.value,
                accessToken = generateAccessToken()
            )

            try {
                if (mode.value == AccessDialogMode.EDIT && accessId != null) {
                    alterAccess(accessId!!, access)
                } else if (mode.value == AccessDialogMode.CREATE) {
                    registerAccess(access)
                }
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun carregarCategorias() {
        viewModelScope.launch {
            try {
                val categorias = getAllCategoryNames()
                categoriasDisponiveis.value = listOf("+ Adicionar nova categoria") + categorias
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    fun onCategoriaSelecionada(novaCategoria: String) {
        categoria.value = novaCategoria
    }
}
