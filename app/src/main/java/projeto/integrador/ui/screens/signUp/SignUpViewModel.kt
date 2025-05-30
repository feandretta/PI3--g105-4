package projeto.integrador.ui.screens.signUp

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import projeto.integrador.utilities.Cadastro

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

    fun signUp(context: Context, onResult: (Boolean, String) -> Unit) {
        val nome = nomeState.text.toString()
        val email = emailState.text.toString()
        val senha = senhaState.text.toString()
        val confirmarSenha = confirmarSenhaState.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val resultado = Cadastro(context, nome, email, senha, confirmarSenha)
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
    }
}
