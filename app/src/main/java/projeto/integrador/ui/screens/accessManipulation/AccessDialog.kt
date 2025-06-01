package projeto.integrador.ui.screens.accessManipulation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    onDismiss: () -> Unit,
    onSaveComplete: () -> Unit = {},
    viewModel: AccessDialogViewModel = viewModel()
) {
    val showCategoriaDialog = remember { mutableStateOf(false) }
    val isEditing = viewModel.mode.value != AccessDialogMode.VIEW
    val isFormValid = viewModel.isFormValid()

    LaunchedEffect(Unit) {
        viewModel.carregarCategorias()
    }

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
                        value = viewModel.senha.value,
                        onValueChange = { viewModel.senha.value = it },
                        label = "Senha",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing
                    )

                    SelectorField(
                        label = "Categoria",
                        selectedItem = viewModel.categoria.value,
                        items = viewModel.categoriasDisponiveis.value,
                        onItemSelected = { categoria ->
                            if (categoria.startsWith("+")) {
                                showCategoriaDialog.value = true
                            } else {
                                viewModel.onCategoriaSelecionada(categoria)
                            }
                        },
                        enabled = isEditing
                    )

                    OutlinedTextField(
                        value = viewModel.url.value,
                        onValueChange = { viewModel.url.value = it },
                        label = { Text("URL") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing
                    )

                    OutlinedTextField(
                        value = viewModel.email.value,
                        onValueChange = { viewModel.email.value = it },
                        label = { Text("Email") },
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

                    CriarCategoriaDialog(
                        showDialog = showCategoriaDialog.value,
                        onDismiss = { showCategoriaDialog.value = false },
                        onCategoryCreated = {
                            viewModel.onCategoriaSelecionada(it)
                            viewModel.carregarCategorias()
                            showCategoriaDialog.value = false
                        }
                    )
                }
            }
        }
    }
}
