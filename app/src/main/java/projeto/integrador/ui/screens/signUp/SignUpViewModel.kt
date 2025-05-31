package projeto.integrador.ui.screens.signUp

import android.content.Context
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import projeto.integrador.data.model.User
import projeto.integrador.utilities.funcs.Cadastro

class SignUpViewModel : ViewModel() {
    val nomeState = TextFieldState()
    val emailState = TextFieldState()
    val senhaState = TextFieldState()
    val confirmarSenhaState = TextFieldState()

    var senhaVisivel = mutableStateOf(false)
    var confirmarSenhaVisivel = mutableStateOf(false)
    var termosAceitos = mutableStateOf(false)

    fun toggleSenhaVisivel() {
        senhaVisivel.value = !senhaVisivel.value
    }

    fun toggleConfirmarSenhaVisivel() {
        confirmarSenhaVisivel.value = !confirmarSenhaVisivel.value
    }
    @RequiresPermission("android.permission.READ_PHONE_STATE")
    fun signUp(context: Context, onResult: (Boolean, String) -> Unit) {


        if(senhaState.text.toString() == confirmarSenhaState.text.toString()){
            var usuario: User = User(nomeState.text.toString(),
                                     emailState.text.toString(),
                                     senhaState.text.toString())

            CoroutineScope(Dispatchers.IO).launch {
                val resultado = Cadastro(context, usuario)
                val mensagem = if (resultado) {
                    "Cadastro realizado com sucesso!"
                } else {
                    "Erro ao realizar o cadastro!"
                }
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show()
                    onResult(resultado, mensagem)
                }
            }
            return
        }
        return
    }
}
