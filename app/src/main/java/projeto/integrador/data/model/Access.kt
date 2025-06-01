package projeto.integrador.data.model

data class Access(
    var nome: String? = null,
    var categoria: String? = null,
    var dominio: String? = null,
    var email: String? = null,
    var senha: String? = null,
    var descricao: String? = null,
    var accessToken: String? = null
)