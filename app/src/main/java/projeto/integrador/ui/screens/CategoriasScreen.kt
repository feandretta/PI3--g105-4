// Declaração do pacote e imports necessários
package projeto.integrador.ui.screens

// Imports do AndroidX Compose para UI
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp

// Imports para gerenciamento de ciclo de vida

// Imports para navegação
import androidx.navigation.NavHostController

// Imports do Firebase (autenticação e banco de dados)
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Imports para corrotinas

// Imports locais
import projeto.integrador.data.model.Category
import projeto.integrador.utilities.CriarCategoriaDialog

/**
 * Tela que exibe a lista de categorias do usuário.
 * 
 * @param navController Controlador de navegação para gerenciar a navegação entre telas
 * @param modifier Modificador para personalizar o layout da tela
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Estado para armazenar a lista de categorias
    var categorias by remember { mutableStateOf<List<Category>>(emptyList()) }
    
    // Estado para controlar o carregamento dos dados
    var isLoading by remember { mutableStateOf(true) }
    
    // Estado para armazenar mensagens de erro, se houver
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Estado para controlar a exibição do diálogo de adicionar categoria
    var showAddDialog by remember { mutableStateOf(false) }
    
    // Obtém o ID do usuário atual ou string vazia se não estiver autenticado
    val userId = Firebase.auth.currentUser?.uid ?: ""
    
    // Referência ao banco de dados Firestore
    val db = Firebase.firestore
    
    // Referência ao dono do ciclo de vida atual da tela
    val lifecycleOwner = LocalLifecycleOwner.current

    // Efeito colateral que configura um listener em tempo real para as categorias
    // Este efeito é executado quando o userId muda e é limpo quando o componente é desmontado
    DisposableEffect(userId) {
        // Se não houver usuário autenticado, não faz nada
        if (userId.isEmpty()) return@DisposableEffect onDispose {}
        
        // Cria uma consulta ao Firestore para buscar as categorias do usuário atual
        val query = db.collection("usuarios")
            .document(userId)  // Documento do usuário atual
            .collection("categories")  // Subcoleção de categorias
            .orderBy("createdAt")  // Ordena por data de criação (mais antigas primeiro)
        
        // Cria um listener que será notificado sempre que houver mudanças na consulta
        val subscription = query.addSnapshotListener { snapshot, error ->
            // Indica que o carregamento terminou
            isLoading = false
            
            // Verifica se houve erro na consulta
            error?.let {
                errorMessage = "Erro ao carregar categorias: ${it.message}"
                return@addSnapshotListener
            }
            
            // Processa o resultado da consulta
            snapshot?.let { querySnapshot ->
                // Converte cada documento em um objeto Category
                categorias = querySnapshot.documents.mapNotNull { doc ->
                    try {
                        // Converte o documento para um objeto Category
                        doc.toObject(Category::class.java)?.apply {
                            doc.id.let { id ->
                                // Se sua classe Category tiver um campo para o ID do documento
                                // você pode defini-lo aqui, por exemplo:
                                // this.documentId = id
                            }
                        }
                    } catch (e: Exception) {
                        // Em caso de erro na conversão, retorna null (será filtrado pelo mapNotNull)
                        null
                    }
                }
                // Limpa a mensagem de erro em caso de sucesso
                errorMessage = null
            }
        }
        
        // Retorna uma função de limpeza que será chamada quando o efeito for desfeito
        // Isso garante que o listener seja removido quando o componente for desmontado
        onDispose {
            subscription.remove()
        }
    }

    // Componente de diálogo para adicionar uma nova categoria
    // Este diálogo é controlado pelo estado showAddDialog
    CriarCategoriaDialog(
        showDialog = showAddDialog,  // Controla se o diálogo está visível
        onDismiss = { showAddDialog = false },  // Chamado quando o diálogo é fechado
        onCategoryCreated = {
            // Quando uma nova categoria é criada, não precisamos fazer nada aqui
            // porque o snapshotListener já irá capturar a mudança e atualizar a lista
        }
    )

    // Estrutura básica da tela com AppBar e conteúdo
    Scaffold(
        // Barra superior da tela
        topBar = {
            TopAppBar(
                // Título da barra superior
                title = { Text("Minhas Categorias") },
                // Ações na barra superior (lado direito)
                actions = {
                    // Botão para adicionar nova categoria
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,  // Ícone de adicionar
                            contentDescription = "Adicionar Categoria"  // Descrição para acessibilidade
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Conteúdo principal da tela
        Box(
            modifier = Modifier
                .fillMaxSize()  // Ocupa todo o espaço disponível
                .padding(innerPadding)  // Aplica o padding interno do Scaffold
        ) {
            // Exibe diferentes estados da tela com base no estado atual
            when {
                // Estado de carregamento
                isLoading -> {
                    // Mostra um indicador de carregamento centralizado
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)  // Centraliza na tela
                            .size(48.dp)  // Define o tamanho do indicador
                    )
                }
                // Estado de erro
                errorMessage != null -> {
                    // Mostra a mensagem de erro centralizada
                    Text(
                        text = errorMessage ?: "Erro desconhecido",  // Mensagem de erro ou padrão
                        modifier = Modifier.align(Alignment.Center)  // Centraliza o texto
                    )
                }
                // Estado quando não há categorias
                categorias.isEmpty() -> {
                    // Mensagem informativa quando não há categorias
                    Text(
                        text = "Nenhuma categoria cadastrada.\nClique no ícone + para adicionar.",
                        modifier = Modifier
                            .align(Alignment.Center)  // Centraliza o texto
                            .padding(16.dp)  // Adiciona padding ao redor do texto
                    )
                }
                // Estado normal (com categorias)
                else -> {
                    // Lista rolável de categorias usando LazyColumn para melhor desempenho
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()  // Ocupa todo o espaço disponível
                            .padding(16.dp),  // Adiciona padding ao redor da lista
                        verticalArrangement = Arrangement.spacedBy(8.dp)  // Espaçamento entre itens
                    ) {
                        // Para cada categoria na lista, cria um item na lista
                        items(categorias) { categoria ->
                            // Card que contém as informações da categoria
                            Card(
                                modifier = Modifier.fillMaxWidth()  // Ocupa toda a largura
                            ) {
                                // Linha com as informações da categoria
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()  // Ocupa toda a largura
                                        .padding(16.dp),  // Espaçamento interno
                                    horizontalArrangement = Arrangement.SpaceBetween,  // Espaço entre os elementos
                                    verticalAlignment = Alignment.CenterVertically  // Centraliza verticalmente
                                ) {
                                    // Linha com o ícone de cor e o nome da categoria
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Caixa colorida que representa a cor da categoria
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)  // Tamanho fixo
                                                .padding(4.dp)  // Espaçamento interno
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))  // Espaço entre o ícone e o texto
                                        Text(text = categoria.name)  // Nome da categoria
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
