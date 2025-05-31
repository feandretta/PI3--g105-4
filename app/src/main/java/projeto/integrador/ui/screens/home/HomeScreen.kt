package projeto.integrador.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
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

    val accessList by viewModel.accessItems.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAccessItems()
    }

    if (showAccessDialog) {
        AccessDialog(
            mode = accessDialogViewModel.mode.value,
            accessId = accessDialogViewModel.accessId ?: "",
            onDismiss = {
                showAccessDialog = false
                viewModel.loadAccessItems()
            },
            onSaveComplete = {
                showAccessDialog = false
                viewModel.loadAccessItems()
            },
            viewModel = accessDialogViewModel
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "Categorias (em breve)",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Super ID") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("settings") }) {
                            Icon(Icons.Default.Person, contentDescription = null)
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    listOf(
                        NavItem("Senhas", Icons.Default.Key),
                        NavItem("Scanner", Icons.Default.QrCodeScanner),
                        NavItem("Configurações", Icons.Default.Settings)
                    ).forEach { item ->
                        NavigationBarItem(
                            selected = selectedBottomBarItem == item.route,
                            onClick = { selectedBottomBarItem = item.route },
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(item.route) }
                        )
                    }
                }
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
                        AccessListContent(
                            accessList = accessList,
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
                            }
                        )
                    }

                    "Scanner" -> QrCodeScannerScreen(innerPadding)

                    "Configurações" -> ConfigScreen(innerPadding)
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
