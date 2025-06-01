import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import projeto.integrador.utilities.validation

class SignInViewModel : ViewModel() {
    val emailState = TextFieldState()
    val senhaState = TextFieldState()

    var senhaVisivel = mutableStateOf(false)

    fun toggleSenhaVisivel() {
        senhaVisivel.value = !senhaVisivel.value
    }

    fun signIn(context: Context, onResult: (Boolean) -> Unit) {
        val email = emailState.text.toString()
        val senha = senhaState.text.toString()

        validation(context, email, senha) { success, message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            onResult(success)
        }
    }
}
