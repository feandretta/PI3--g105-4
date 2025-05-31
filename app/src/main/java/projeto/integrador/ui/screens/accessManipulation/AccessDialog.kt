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
import projeto.integrador.utilities.CriarCategoriaDialog

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AccessDialog(
    onDismiss: () -> Unit,
    onSaveComplete: () -> Unit = {},
    mode: AccessDialogMode,
    accessId: String = "",
    viewModel: AccessDialogViewModel = viewModel()
) {
    val showCategoriaDialog = remember { mutableStateOf(false) }
    val isEditing = viewModel.mode.value != AccessDialogMode.VIEW
    val isFormValid = viewModel.isFormValid()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
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
                    RequiredTextField(
                        value = viewModel.nome.value,
                        onValueChange = { viewModel.nome.value = it },
                        label = "Nome",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing
                    )

                    RequiredTextField(
                        value = viewModel.url.value,
                        onValueChange = { viewModel.url.value = it },
                        label = "URL",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing
                    )

                    RequiredTextField(
                        value = viewModel.email.value,
                        onValueChange = { viewModel.email.value = it },
                        label = "Email",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing
                    )

                    RequiredTextField(
                        value = viewModel.senha.value,
                        onValueChange = { viewModel.senha.value = it },
                        label = "Senha",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing
                    )

                    OutlinedTextField(
                        value = viewModel.descricao.value,
                        onValueChange = { viewModel.descricao.value = it },
                        label = { Text("Descrição") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        enabled = isEditing
                    )

                    Text(
                        text = "Categoria: ${viewModel.categoria.value}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (isEditing) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { showCategoriaDialog.value = true }
                        ) {
                            Text("Selecionar Categoria")
                        }
                    }

                    CriarCategoriaDialog(
                        showDialog = showCategoriaDialog.value,
                        onDismiss = { showCategoriaDialog.value = false },
                        //onCategoryCreated = { viewModel.onCategoriaSelecionada(it) }
                    )
                }
            }
        }
    }
}
