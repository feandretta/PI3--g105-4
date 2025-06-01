package projeto.integrador.utilities

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object CryptoManager {

    //Cria uma chave e guarda no Keystore, so é chamada quando nao tem chave ainda
    private fun createSecretKey(): SecretKey {
        //Cria um key generator do java com o tipo da criptografia a ser usada: AES
        //e o provedor da criptografia: AndroidKeyStore
        val keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore")

        //Cria um keyGenSpec(Objeto que vai guardar os parametros da criptografia) e passa pra ele:
        //o nome(alias) que ele vai ter no Keystore, o proposito dessa chave no keystore (ENCRYPT e DECRYPT),
        //o modo de criptografia (CBC) e o padding (PKCS7)
        val keyGenSpec = KeyGenParameterSpec.Builder(
            "chaveCriptografia",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .apply {
                setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                setKeySize(256)
            }.build()

        //Inicializa o keyGenerator com os specs declarados acima, gera uma key e retorna ela
        keyGenerator.init(keyGenSpec)
        return keyGenerator.generateKey()
    }

    //Busca a chave no Keystore
    private fun getSecretKey(): SecretKey {
        //Instancia o Keystore
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

        //Tenta obter a entrada da chave com o alias "chaveCriptografia"
        //podendo ser nulo caso nao tenha achado a entrada
        val keyStoreEntry = keyStore.getEntry("chaveCriptografia", null) as? KeyStore.SecretKeyEntry

        //Pega a chave da entrada, que pode ser nula
        val key = keyStoreEntry?.secretKey

        if (key != null) {
            return key
        } else {
            return createSecretKey()
        }
    }

    //Funcao que recebe uma string, criptografa e retorna a string criptografada
    fun encrypt(string: String): String {
        //Usa o Cipher do java para fazer a criptografia do tipo "AES/CBC/PKCS7Padding"
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")

        //Inicia a criptografia no modo ENCRYPT e com a chave que vem de getSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        //Transforma a string em um array de bytes para prepará-la para a criptografia
        val byteArray = string.toByteArray(Charsets.UTF_8)

        //Criptografa o array usando o cipher.doFinal()
        val encryptedBytes = cipher.doFinal(byteArray)

        //Pega o IV usado na criptografia (necessário para descriptografar)
        val iv = cipher.iv

        //Concatena IV + encryptedBytes e codifica em Base64
        val combined = iv + encryptedBytes
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    //Funcao que recebe uma string criptografada, descriptografa e retorna a string original
    fun decrypt(encryptedText: String): String {
        //Decodifica a string criptografada de Base64 para bytes
        val combined = Base64.decode(encryptedText, Base64.NO_WRAP)

        //Extrai o IV (primeiros 16 bytes) e o conteúdo criptografado
        val iv = combined.copyOfRange(0, 16)
        val encryptedBytes = combined.copyOfRange(16, combined.size)

        //Usa o Cipher do java para fazer a descriptografia do tipo "AES/CBC/PKCS7Padding"
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")

        //Inicia a descriptografia no modo DECRYPT, com a chave e o IV usados na criptografia
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), IvParameterSpec(iv))

        //Descriptografa os dados
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        //Transforma o array de bytes na string original
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
