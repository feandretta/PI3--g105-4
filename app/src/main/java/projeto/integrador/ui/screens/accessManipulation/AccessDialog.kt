package projeto.integrador.ui.screens.accessManipulation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import projeto.integrador.ui.screens.components.RequiredTextField
import projeto.integrador.ui.screens.components.SelectorField
import projeto.integrador.utilities.CriarCategoriaDialog

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AccessDialog(
    onDismiss: () -> Unit, // Função chamada ao fechar o diálogo
    onSaveComplete: () -> Unit = {}, // Função opcional executada após salvar
    viewModel: AccessDialogViewModel = viewModel() // ViewModel da tela
) {
    val showCategoriaDialog = remember { mutableStateOf(false) } // Controla se o diálogo de criar categoria será mostrado
    val isEditing = viewModel.mode.value != AccessDialogMode.VIEW // Verifica se está em modo de edição ou criação
    val isFormValid = viewModel.isFormValid() // Verifica se os campos obrigatórios estão preenchidos

    // Carrega as categorias ao abrir o diálogo
    LaunchedEffect(Unit) {
        viewModel.carregarCategorias()
    }

    // Cria o diálogo ocupando toda a tela
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                // Barra superior com título, botão voltar e salvar
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                when (viewModel.mode.value) {
                                    AccessDialogMode.CREATE -> "Novo Acesso"
                                    AccessDialogMode.EDIT -> "Editar Acesso"
                                    AccessDialogMode.VIEW -> "Detalhes do Acesso"
                                }
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                            }
                        },
                        actions = {
                            // Botão "Salvar" aparece apenas no modo de edição ou criação
                            if (isEditing) {
                                TextButton(
                                    enabled = isFormValid,
                                    onClick = {
                                        viewModel.salvar {
                                            onSaveComplete()
                                            onDismiss()
                                        }
                                    }
                                ) {
                                    Text("Salvar")
                                }
                            }
                        }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Campo obrigatório: Nome
                    RequiredTextField(
                        value = viewModel.nome.value,
                        onValueChange = { viewModel.nome.value = it },
                        label = "Nome",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing
                    )

                    // Campo obrigatório: Senha
                    RequiredTextField(
                        value = viewModel.senha.value,
                        onValueChange = { viewModel.senha.value = it },
                        label = "Senha",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing
                    )

                    // Campo de seleção de categoria com opção de adicionar nova
                    SelectorField(
                        label = "Categoria",
                        selectedItem = viewModel.categoria.value,
                        items = viewModel.categoriasDisponiveis.value,
                        onItemSelected = { categoria ->
                            if (categoria.startsWith("+")) {
                                showCategoriaDialog.value = true // Abre diálogo de nova categoria
                            } else {
                                viewModel.onCategoriaSelecionada(categoria)
                            }
                        },
                        enabled = isEditing
                    )

                    // Campo opcional: URL
                    OutlinedTextField(
                        value = viewModel.url.value,
                        onValueChange = { viewModel.url.value = it },
                        label = { Text("URL") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing
                    )

                    // Campo opcional: Email
                    OutlinedTextField(
                        value = viewModel.email.value,
                        onValueChange = { viewModel.email.value = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing
                    )

                    // Campo opcional: Descrição (com altura fixa)
                    OutlinedTextField(
                        value = viewModel.descricao.value,
                        onValueChange = { viewModel.descricao.value = it },
                        label = { Text("Descrição") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        enabled = isEditing
                    )

                    // Diálogo de criação de nova categoria
                    CriarCategoriaDialog(
                        showDialog = showCategoriaDialog.value,
                        onDismiss = { showCategoriaDialog.value = false },
                        onCategoryCreated = {
                            viewModel.onCategoriaSelecionada(it) // Seleciona nova categoria
                            viewModel.carregarCategorias() // Recarrega lista
                            showCategoriaDialog.value = false
                        }
                    )
                }
            }
        }
    }
}
