package projeto.integrador.ui.screens.accessManipulation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import projeto.integrador.config.generateAccessToken
import projeto.integrador.data.model.Access
import projeto.integrador.utilities.CryptoManager
import projeto.integrador.utilities.alterAccess
import projeto.integrador.utilities.getAccessByUser
import projeto.integrador.utilities.getAllCategoryNames
import projeto.integrador.utilities.registerAccess

// Enum para definir o modo atual da tela de acesso (visualizar, editar ou criar)
enum class AccessDialogMode {
    VIEW, EDIT, CREATE
}

// ViewModel responsável pela lógica da tela de manipulação de acessos
class AccessDialogViewModel : ViewModel() {

    // Estados mutáveis para armazenar os dados dos campos
    val nome = mutableStateOf("")
    val url = mutableStateOf("")
    val email = mutableStateOf("")
    val senha = mutableStateOf("")
    val descricao = mutableStateOf("")
    val categoria = mutableStateOf("Selecionar Categoria")

    // Lista de categorias disponíveis para o usuário
    val categoriasDisponiveis = mutableStateOf<List<String>>(emptyList())

    // Define o modo atual do formulário e ID do acesso (se estiver editando)
    val mode = mutableStateOf(AccessDialogMode.CREATE)
    var accessId: String? = null

    // Carrega os dados de um objeto Access nos estados do formulário
    fun loadAccessData(access: Access, id: String?, mode: AccessDialogMode) {
        this.mode.value = mode
        this.accessId = id
        nome.value = access.nome.orEmpty()
        url.value = access.dominio.orEmpty()
        email.value = access.email.orEmpty()
        descricao.value = access.descricao.orEmpty()
        categoria.value = access.categoria.orEmpty()

        // Descriptografa a senha, se houver
        if (access.senha.orEmpty() != "") {
            senha.value = CryptoManager.decrypt(access.senha.toString())
        } else {
            senha.value = access.senha.orEmpty()
        }
    }

    // Verifica se os campos obrigatórios estão preenchidos
    fun isFormValid(): Boolean {
        return nome.value.isNotBlank() && senha.value.isNotBlank()
    }

    // Carrega um acesso específico pelo ID e aplica os dados no formulário
    fun loadAccessById(id: String, mode: AccessDialogMode) {
        viewModelScope.launch {
            try {
                val snapshot = getAccessByUser(id)
                val access = snapshot.toObject(Access::class.java)
                if (access != null) {
                    loadAccessData(access, id, mode)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Salva ou edita um acesso, dependendo do modo atual
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun salvar(onComplete: () -> Unit) {
        viewModelScope.launch {
            // Cria o objeto Access com os dados atuais do formulário
            val access = Access(
                nome = nome.value,
                categoria = categoria.value,
                dominio = url.value,
                email = email.value,
                senha = senha.value,
                descricao = descricao.value,
                accessToken = generateAccessToken() // Gera novo accessToken
            )

            try {
                if (mode.value == AccessDialogMode.EDIT && accessId != null) {
                    // Se estiver editando, atualiza o acesso existente
                    alterAccess(accessId!!, access)
                } else if (mode.value == AccessDialogMode.CREATE) {
                    // Se estiver criando, registra novo acesso
                    registerAccess(access)
                }
                onComplete() // Executa função de finalização
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Carrega todas as categorias disponíveis do banco de dados
    fun carregarCategorias() {
        viewModelScope.launch {
            try {
                val categorias = getAllCategoryNames()
                // Adiciona opção de criar nova categoria no topo da lista
                categoriasDisponiveis.value = listOf("+ Adicionar nova categoria") + categorias
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Atualiza a categoria selecionada
    fun onCategoriaSelecionada(novaCategoria: String) {
        categoria.value = novaCategoria
    }
}
