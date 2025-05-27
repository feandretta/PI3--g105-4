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
import projeto.integrador.utilities.funcs.accessRegister

class AddAccessViewModel : ViewModel() {
    val nome = mutableStateOf(TextFieldState())
    val categoria = mutableStateOf("Selecionar Categoria")
    val url = mutableStateOf(TextFieldState())
    val email = mutableStateOf(TextFieldState())
    val senha = mutableStateOf(TextFieldState())
    val descricao = mutableStateOf(TextFieldState())

    fun onCategoriaSelecionada(novaCategoria: String) {
        categoria.value = novaCategoria
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun salvar() {
        viewModelScope.launch {
            val access = Access(
                nome = nome.value.text.toString(),
                //categoria = categoria.value.text.toString(),
                categoria = categoria.value,
                dominio = url.value.text.toString(),
                email = email.value.text.toString(),
                senha = senha.value.text.toString(),
                descricao = descricao.value.text.toString()
            )
            val result = accessRegister(access)

            Log.d("TesteCadastro", if (result) "Cadastro realizado!" else "Erro ao cadastrar.")
        }
    }
}