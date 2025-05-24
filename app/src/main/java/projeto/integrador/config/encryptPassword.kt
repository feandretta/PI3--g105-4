package projeto.integrador.config

import java.security.MessageDigest

fun encryptPassword(senha:String): ByteArray {
    val senhaRecebida:ByteArray = senha.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val senhaCripto = md.digest(senhaRecebida)

    return senhaCripto
}