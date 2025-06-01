package projeto.integrador.ui.screens.unlock

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class UnlockViewModel : ViewModel() {
    val senhaState = TextFieldState()
    var senhaVisivel = mutableStateOf(false)
        private set

    fun toggleSenhaVisivel() {
        senhaVisivel.value = !senhaVisivel.value
    }

    fun unlock(context: Context, onResult: (Boolean) -> Unit) {
        val password = senhaState.text
        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser!!.email!!

        auth.signInWithEmailAndPassword(email, password.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true)
                } else {
                    Toast.makeText(context, "Senha incorreta", Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
            }
    }
}
