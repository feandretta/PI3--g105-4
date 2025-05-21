package projeto.integrador.utilities.funcs

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.provider.Settings
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import projeto.integrador.config.criptografar
private const val PERMISSION_REQUEST_CODE = 101

// Função para verificar se a permissão foi concedida
fun checkPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_PHONE_STATE
    ) == PackageManager.PERMISSION_GRANTED
}

// Função para solicitar a permissão ao usuário (chame dentro de uma Activity)
fun requestPhoneStatePermission(activity: Activity) {
    if (!checkPermission(activity)) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            PERMISSION_REQUEST_CODE
        )
    }
}

// Objeto utilitário para obter o identificador do dispositivo
object DeviceUtils {

    @SuppressLint("HardwareIds", "MissingPermission")
    fun getDeviceImei(context: Context): String {
        return try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // A partir do Android 10, o IMEI não pode ser acessado diretamente
                getAndroidID(context)
            } else {
                if (checkPermission(context)) {
                    getDeviceImei(context)
                    telephonyManager.imei ?: getAndroidID(context)
                } else {
                    "PERMISSION_DENIED"
                }
            }
        } catch (e: SecurityException) {
            "PERMISSION_DENIED"
        } catch (e: Exception) {
            "UNKNOWN"
        }
    }

    fun getAndroidID(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}

/**
 * Cria categorias pré-definidas para um novo usuário
 * @param userId ID do usuário para o qual as categorias serão criadas
 */
private suspend fun createDefaultCategories(userId: String) {
    val db = Firebase.firestore
    val categories = listOf(
        hashMapOf(
            "name" to "Sites Web",
            "color" to "#2196F3", // Verde
            "createdAt" to com.google.firebase.Timestamp.now()
        ),
        hashMapOf(
            "name" to "Apps",
            "color" to "#4CAF50", // Azul
            "createdAt" to com.google.firebase.Timestamp.now()
        ),
        hashMapOf(
            "name" to "Alimentação",
            "color" to "#9C27B0", // Roxo
            "createdAt" to com.google.firebase.Timestamp.now()
        ),
        hashMapOf(
            "name" to "Lazer",
            "color" to "#9C27B0", // Roxo
            "createdAt" to com.google.firebase.Timestamp.now()
    )
    )

    // Adiciona cada categoria ao Firestore
    categories.forEach { category ->
        try {
            db.collection("usuarios")
                .document(userId)
                .collection("categories")
                .add(category)
                .await()
        } catch (e: Exception) {
            Log.e("Cadastro", "Erro ao criar categoria padrão: ${e.message}")
        }
    }
}

/**
 * Função suspensa para criar usuário e salvar os dados no Firestore
 * @param context Contexto da aplicação
 * @param nome Nome do usuário
 * @param email Email do usuário
 * @param senha Senha do usuário
 * @param confirmarSenha Confirmação da senha
 * @return Boolean indicando se o cadastro foi bem-sucedido
 */
@RequiresPermission("android.permission.READ_PHONE_STATE")
suspend fun Cadastro(
    context: Context,
    nome: String,
    email: String,
    senha: String,
    confirmarSenha: String
): Boolean {
    if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) return false
    if (senha.length < 6) return false
    if (senha != confirmarSenha) return false

    val auth = Firebase.auth
    val db = Firebase.firestore

    println(criptografar(senha))



    return try {
        val authResult = auth.createUserWithEmailAndPassword(email, senha).await()
        val uid = authResult.user?.uid ?: return false
        val imei = DeviceUtils.getDeviceImei(context)


        val userMap = hashMapOf(
            "nome" to nome,
            "email" to email,
            "uid" to uid,
            "imei" to imei
        )

        // Salva os dados do usuário no Firestore
        db.collection("usuarios")
            .document(uid)
            .set(userMap)
            .await()

        // Cria as categorias pré-definidas para o novo usuário
        createDefaultCategories(uid)

        true
    } catch (e: Exception) {

        e.printStackTrace()

        Log.e("Cadastro", "Erro ao cadastrar usuário", e)

        false
    }
}
