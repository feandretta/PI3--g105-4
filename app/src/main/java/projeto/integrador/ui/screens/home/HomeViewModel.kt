package projeto.integrador.ui.screens.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import projeto.integrador.data.model.Access
import projeto.integrador.utilities.funcs.accessRegister

class HomeViewModel : ViewModel() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun teste() {
        viewModelScope.launch {
            val teste = Access("Senhas", "www.teste.com", "Junin@bol.com.br", "123abc")
            val result = accessRegister(teste)

            Log.d("TesteCadastro", if (result) "Cadastro realizado!" else "Erro ao cadastrar.")
        }
    }
}