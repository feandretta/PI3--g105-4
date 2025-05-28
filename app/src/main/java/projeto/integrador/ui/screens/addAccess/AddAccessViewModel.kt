package projeto.integrador.ui.screens.addAccess

import android.os.Build
import android.system.Os.access
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import projeto.integrador.data.model.Access
import projeto.integrador.utilities.funcs.CryptoUtils
import projeto.integrador.utilities.funcs.accessRegister

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
            val senhaCriptografada = CryptoUtils.encrypt(senha.toString())

            val access = Access(
                nome = nome.toString(),
                categoria = categoria.toString(),
                dominio = url.toString(),
                email = email.toString(),
                senha = senhaCriptografada,
                descricao = descricao.toString()
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