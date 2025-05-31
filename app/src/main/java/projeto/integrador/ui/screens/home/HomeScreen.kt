import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import projeto.integrador.data.model.Access
import projeto.integrador.ui.screens.ConfigScreen
import projeto.integrador.ui.screens.components.AccessCard
import projeto.integrador.ui.screens.components.QrCodeScannerScreen
import projeto.integrador.ui.screens.home.HomeViewModel

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



    //Itens de navegação da bottom bar
    data class NavItem(val route: String, val icon: ImageVector)
    val navItems = listOf(
        NavItem("Senhas", Icons.Default.Key),
        NavItem("Scanner", Icons.Default.QrCodeScanner),
        NavItem("Configurações", Icons.Default.Settings)
    )

    //Item atualmente selecionado da bottomBar (Comeca com "Senhas")
    var selectedItem by remember { mutableStateOf("Senhas") }

    //Lista de Acessos para mostrar os Cards
    val accessList by viewModel.accessItems.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAccessItems()
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
                    navItems.forEach { item ->
                        NavigationBarItem(
                            selected = selectedItem == item.route,
                            onClick = { selectedItem = item.route },
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(item.route) }
                        )
                    }
                }
            },
            floatingActionButton = {
                when (selectedItem) {

                    "Senhas" -> {
                        FloatingActionButton(
                            onClick = { navController.navigate("addAccess") }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }
                }

            },
            content = { innerPadding ->
                when (selectedItem) {

                    "Scanner" -> { QrCodeScannerScreen(innerPadding) }

                    "Senhas" -> { AccessListContent(accessList = accessList, innerPadding) }

                    "Configurações" -> { ConfigScreen(innerPadding) }

                }
            }
        )
    }
}

@Composable
fun AccessListContent(
    accessList: List<Access>,
    padding: PaddingValues
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
            items(accessList) { access ->
                AccessCard(access = access)
            }
        }
    }
}



