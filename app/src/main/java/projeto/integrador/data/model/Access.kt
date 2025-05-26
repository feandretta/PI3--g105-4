package projeto.integrador.data.model

class Access(categoria: String, dominio: String, email: String, senha: String){
    var nome: String? = null
    var categoria: String? = categoria
    var parceiro: String? = dominio
    var email: String? = email
    var salt: String? = null
    var senha: String? = senha
    var descricao: String? = null
    var accessToken: String? = null

    fun AccessIsEmpty(): Boolean{
        if (senha?.isEmpty() == true || categoria?.isEmpty() == true || nome?.isEmpty() == true) return true
        return false
    }

}