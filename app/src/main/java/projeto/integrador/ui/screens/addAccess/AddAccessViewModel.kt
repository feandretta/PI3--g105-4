package projeto.integrador.ui.screens.addAccess

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import projeto.integrador.config.generateAccessToken
import projeto.integrador.data.model.Access
import projeto.integrador.utilities.registerAccess

class AddAccessViewModel : ViewModel() {
    val nome = mutableStateOf("")
    val url = mutableStateOf("")
    val email = mutableStateOf("")
    val senha = mutableStateOf("")
    val descricao = mutableStateOf("")
    val categoria = mutableStateOf("Selecionar Categoria")
    val result = mutableStateOf("")

    fun onCategoriaSelecionada(novaCategoria: String) {
        categoria.value = novaCategoria
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun salvar() {
        viewModelScope.launch {

            val access = Access(
                nome = nome.value.toString(),
                categoria = categoria.value.toString(),
                dominio = url.value.toString(),
                email = email.value.toString(),
                senha = senha.value.toString(),
                descricao = descricao.value.toString(),
                accessToken = generateAccessToken()
            )
            val result = registerAccess(access)
        }
    }

    fun isFormValid(): Boolean {
        return nome.value.isNotBlank() &&
                url.value.isNotBlank() &&
                email.value.isNotBlank() &&
                senha.value.isNotBlank()
    }
}