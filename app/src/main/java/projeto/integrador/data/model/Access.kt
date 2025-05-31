package projeto.integrador.data.model

class Access(
    var nome: String? = null,
    var categoria: String? = null,
    var dominio: String? = null,
    var email: String? = null,
    var senha: String? = null,
    var descricao: String? = null,
    var accessToken: String? = null
){
    fun isEmpty(): Boolean{
        return senha?.isEmpty() == true || categoria?.isEmpty() == true || nome?.isEmpty() == true
        return false
    }
}