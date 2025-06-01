package projeto.integrador.data.model

data class User(
    var nome: String? = null,
    var email: String? = null,
    var senha: String? = null,
    var imei: String? = null,
    var uid: String? = null
){
    fun isEmpty(): Boolean{
        return senha?.isEmpty() == true || email?.isEmpty() == true || nome?.isEmpty() == true
        return false
    }
}
