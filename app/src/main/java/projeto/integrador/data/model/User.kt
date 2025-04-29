package projeto.integrador.data.model

class Usuario(nome: String, email: String, senha: String, confirmarSenha: String) {
    var nome: String = ""
    var email: String = ""
    var senha: String = ""
    var confirmarSenha: String = ""
}

class ContaSite(categoria: String ,dominio: String, email: String, senha: String){
    var categoria: String = ""
    var dominio: String = ""
    var email: String = ""
    var senha: String = ""

}