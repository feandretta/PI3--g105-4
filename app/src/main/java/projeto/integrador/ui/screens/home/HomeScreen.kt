package projeto.integrador.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import projeto.integrador.ui.screens.ConfigScreen
import projeto.integrador.ui.screens.accessManipulation.AccessDialog
import projeto.integrador.ui.screens.accessManipulation.AccessDialogMode
import projeto.integrador.ui.screens.accessManipulation.AccessDialogViewModel
import projeto.integrador.ui.screens.components.AccessCard
import projeto.integrador.ui.screens.components.QrCodeScannerScreen

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedBottomBarItem by remember { mutableStateOf("Senhas") }

    // Estados para o diálogo de acesso
    val accessDialogViewModel: AccessDialogViewModel = viewModel()
    var showAccessDialog by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var accessIdToDelete by remember { mutableStateOf<String?>(null) }

    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val accessList by viewModel.accessItems.collectAsState()
    val categoryList by viewModel.categoryNames

    LaunchedEffect(Unit) {
        viewModel.loadAccessItems()
        viewModel.loadCategoryNames()
    }

    if (showAccessDialog) {
        AccessDialog(
            onDismiss = {
                showAccessDialog = false
                viewModel.loadAccessItems()
            },
            onSaveComplete = {
                showAccessDialog = false
                viewModel.loadAccessItems()
                viewModel.loadCategoryNames()
            },
            viewModel = accessDialogViewModel
        )
    }

    if (showDeleteDialog && accessIdToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                accessIdToDelete = null
            },
            title = { Text("Excluir acesso") },
            text = { Text("Tem certeza que deseja excluir este acesso?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = accessIdToDelete
                        if (id != null) {
                            viewModel.deleteAccessById(
                                id = id,
                                onComplete = {
                                    showDeleteDialog = false
                                    accessIdToDelete = null
                                    viewModel.loadAccessItems() // Atualiza categorias após exclusão
                                },
                                onError = {
                                    // Aqui você pode mostrar um Snackbar ou Toast futuramente
                                    showDeleteDialog = false
                                    accessIdToDelete = null
                                }
                            )
                        }
                    }
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        accessIdToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Filtrar por Categoria",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                // Opção "Todas" para mostrar sem filtro
                Text(
                    text = "Todas",
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable {
                            selectedCategory = null
                            scope.launch { drawerState.close() }
                        },
                    style = if (selectedCategory == null) {
                        MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                    } else {
                        MaterialTheme.typography.bodyLarge
                    }
                )

                // Lista de categorias vindas do ViewModel
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    items(categoryList) { category ->
                        Text(
                            text = category,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .clickable {
                                    selectedCategory = category
                                    scope.launch { drawerState.close() }
                                },
                            style = if (selectedCategory == category) {
                                MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                            } else {
                                MaterialTheme.typography.bodyLarge
                            }
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onProfileClick = { navController.navigate("profile") }
                )
            },
            bottomBar = {
                HomeBottomBar(
                    selectedItem = selectedBottomBarItem,
                    onItemSelected = { selectedBottomBarItem = it }
                )
            },
            floatingActionButton = {
                if (selectedBottomBarItem == "Senhas") {
                    FloatingActionButton(
                        onClick = {
                            accessDialogViewModel.mode.value = AccessDialogMode.CREATE
                            accessDialogViewModel.accessId = null
                            accessDialogViewModel.loadAccessData(
                                access = projeto.integrador.data.model.Access(),
                                id = null,
                                mode = AccessDialogMode.CREATE
                            )
                            showAccessDialog = true
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            },
            content = { innerPadding ->
                when (selectedBottomBarItem) {

                    "Senhas" -> {

                        val filteredList = selectedCategory?.let { cat ->
                            accessList.filter { it.access.categoria == cat }
                        } ?: accessList

                        AccessListContent(
                            accessList = filteredList,
                            padding = innerPadding,
                            onView = { id ->
                                accessDialogViewModel.loadAccessById(id, AccessDialogMode.VIEW)
                                showAccessDialog = true
                            },
                            onEdit = { id ->
                                accessDialogViewModel.loadAccessById(id, AccessDialogMode.EDIT)
                                showAccessDialog = true
                            },
                            onDelete = { id ->
                                accessIdToDelete = id
                                showDeleteDialog = true
                            }
                        )
                    }

                    "Scanner" -> QrCodeScannerScreen(innerPadding)

                    "Configurações" -> ConfigScreen(innerPadding,navController)
                }
            }
        )
    }
}


private data class NavItem(val route: String, val icon: ImageVector)


@Composable
fun AccessListContent(
    accessList: List<AccessWithId>,
    padding: PaddingValues,
    onView: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    if (accessList.isEmpty()) {
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Nenhum acesso cadastrado.")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(accessList) { accessWithId ->
                AccessCard(
                    access = accessWithId.access,
                    accessId = accessWithId.id,
                    onView = onView,
                    onEdit = onEdit,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun HomeBottomBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    val items = listOf(
        NavItem("Senhas", Icons.Default.Key),
        NavItem("Scanner", Icons.Default.QrCodeScanner),
        NavItem("Configurações", Icons.Default.Settings)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = selectedItem == item.route,
                onClick = { onItemSelected(item.route) },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.route) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Super ID") },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.Person, contentDescription = null)
            }
        }
    )
}
