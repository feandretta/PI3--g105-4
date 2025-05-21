package projeto.integrador.data.model

class Access(categoria: String, dominio: String, email: String, senha: String){
    var nome: String? = null
    var categoria: String? = null
    var parceiro: String? = null
    var email: String? = null
    var senha: String? = null
    var descricao: String? = null
    var accessToken: String? = null

    fun AccessIsEmpty(): Boolean{
        if (senha?.isEmpty() == true || categoria?.isEmpty() == true || nome?.isEmpty() == true) return true
        return false
    }

}