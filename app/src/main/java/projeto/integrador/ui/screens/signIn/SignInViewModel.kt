package projeto.integrador.ui.screens.signIn

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import projeto.integrador.utilities.funcs.validation

class SignInViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _senha = MutableStateFlow("")
    val senha: StateFlow<String> = _senha

    private val _manterConectado = MutableStateFlow(false)
    val manterConectado: StateFlow<Boolean> = _manterConectado

    private val _senhaVisivel = MutableStateFlow(false)
    val senhaVisivel: StateFlow<Boolean> = _senhaVisivel

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onSenhaChange(newSenha: String) {
        _senha.value = newSenha
    }

    fun onToggleSenhaVisivel() {
        _senhaVisivel.value = !_senhaVisivel.value
    }

    fun onToggleManterConectado() {
        _manterConectado.value = !_manterConectado.value
    }

    fun validarLogin(context: Context, onResult: (Boolean, String) -> Unit) {
        validation(context, _email.value, _senha.value, onResult)
    }
}