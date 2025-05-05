package projeto.integrador.data.model

class Access(categoria: String, dominio: String, email: String, senha: String){
    var categoria: String? = null
    var dominio: String? = null
    var email: String? = null
    var senha: String? = null

    fun AccessIsEmpty(): Boolean{
        if (senha?.isEmpty() == true || categoria?.isEmpty() == true) return false
        return true
    }

}