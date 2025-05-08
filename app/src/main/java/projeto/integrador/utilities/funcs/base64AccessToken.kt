package projeto.integrador.utilities.funcs

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;
import android.util.Base64.DEFAULT

fun generateKey(){
    val generator : KeyGenerator = KeyGenerator.getInstance("AES")

    generator.init(256)

    val secretKey : SecretKey = generator.generateKey()


}

fun encrypt(data : String, secretKey: SecretKey): String? {
    val cipher : Cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val encrypedData : ByteArray = cipher.doFinal(data.toByteArray())
    return Base64.encodeToString(encrypedData, DEFAULT)


}

