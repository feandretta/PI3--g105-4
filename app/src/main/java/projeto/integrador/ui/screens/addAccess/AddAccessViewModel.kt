package projeto.integrador.ui.screens.addAccess

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import projeto.integrador.data.model.Access
import projeto.integrador.utilities.accessRegister

class AddAccessViewModel : ViewModel() {
    val nome = mutableStateOf("")
    val url = mutableStateOf("")
    val email = mutableStateOf("")
    val senha = mutableStateOf("")
    val descricao = mutableStateOf("")
    val categoria = mutableStateOf("Selecionar Categoria")

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
                descricao = descricao.value.toString()
            )
            val result = accessRegister(access)

            Log.d("TesteCadastro", if (result) "Cadastro realizado!" else "Erro ao cadastrar.")
        }
    }

    fun isFormValid(): Boolean {
        return nome.value.isNotBlank() &&
                url.value.isNotBlank() &&
                email.value.isNotBlank() &&
                senha.value.isNotBlank()
    }
}