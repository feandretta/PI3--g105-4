package projeto.integrador.config

import android.util.Base64
import java.security.SecureRandom

fun generateAccessToken(): String {
    val random = SecureRandom()
    val bytes = ByteArray(32) // 256 bits = 32 bytes
    random.nextBytes(bytes)
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}
