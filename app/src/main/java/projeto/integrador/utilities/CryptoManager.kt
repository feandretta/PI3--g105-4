package projeto.integrador.utilities

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object CryptoManager {

    //Cria uma chave e guarda no Keystore, so é chamada quando nao tem chave ainda
    private fun createSecretKey(): SecretKey {

        //Cria um key generator do java com o tipo da criptografia a ser usada: AES/ECB/PKCS5Padding
        //e o provedor da criptografia: Android KeysStore
        val keyGenerator = KeyGenerator.getInstance("AES/ECB/PKCS5Padding", "AndroidKeyStore")

        //Cria um keyGenSpec(Objeto que vai guardar os parametros da critografia) e passa pra ele:
        //o nome(alias) que ele vai ter no Keystore, o proposito dessa chave no keystore(ENCRYPT e DECRYPT),
        //o modo de criptografia(PKCS7) e o tamanho da chave(256)
        val keyGenSpec = KeyGenParameterSpec.Builder(
            "chaveCriptografia",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .apply {
            setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            //OBS: aqui é escrito ENCRYPTION_PADDING_PKCS7 diferente de "AES/ECB/PKCS5Padding",
            //pois o java, por causa de retrocompatibilidade,
            //usa nome diferente, mas funcionalmente são compativeis
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
        //Precisa fazer cast(as) pois get entry retorna um objeto da superclasse KeyStore.Entry
        //e pra acessar a chave precisa do objeto KeyStore.SecretKeyEntry
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
        //Usa o Cipher do java para fazer a criptografia do tipo "AES/ECB/PKCS5Padding"
        //OBS: aqui é escrito "AES/ECB/PKCS5Padding"
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")

        //Inicia a criptografia no modo ENCRYPT e com a chave que vem de getSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        //Transforma a string em um array de bytesFaz para prepara-la para a criptografia
        val byteArray = string.toByteArray(Charsets.UTF_8)

        //Criptografa o array usando o cipher.doFinal()
        //que realiza a operacao ENCRYPT declarada no cipher.init
        val encryptedBytes = cipher.doFinal(byteArray)

        //Tranforma o array de bytes em texto
        val ret = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)

        return ret
    }

    //Funcao que recebe uma string criptografada, descriptografa e retorna a string original
    fun decrypt(encryptedText: String): String {
        //Usa o Cipher do java para fazer a descriptografia do tipo "AES/ECB/PKCS5Padding"
        //OBS: aqui é escrito "AES/ECB/PKCS5Padding" por causa do java
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")

        //Inicia a criptografia no modo DECRYPT e com a chave que vem de getSecretKey()
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey())

        //Transforma a string criptografada em um array de bytes
        val encryptedBytes = Base64.decode(encryptedText, Base64.NO_WRAP)

        //Descriptografa o array usando o cipher.doFinal()
        //que realiza a operacao DECRYPT declarada no cipher.init
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        //Transforma o array de bytes na string original
        val ret = String(decryptedBytes, Charsets.UTF_8)

        return ret
    }
}
