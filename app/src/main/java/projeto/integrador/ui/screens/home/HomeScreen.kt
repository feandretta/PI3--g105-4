import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import projeto.integrador.R
import projeto.integrador.data.model.Access
import projeto.integrador.ui.screens.components.QrCodeScannerScreen
import projeto.integrador.ui.screens.home.HomeViewModel
import projeto.integrador.utilities.funcs.CryptoUtils

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

    var selectedItem by remember { mutableStateOf("home") }

    val navItems = listOf(
        NavItem("home", Icons.Default.Home),
        NavItem("scanner", Icons.Default.QrCodeScanner),
        NavItem("profile", Icons.Default.Person)
    )

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
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("settings") }) {
                            Icon(Icons.Default.Settings, contentDescription = "Configurações")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            selected = selectedItem == item.route,
                            onClick = {
                                selectedItem = item.route
                                when (item.route) {
                                    "scanner" -> selectedItem = item.route
                                    "profile" -> navController.navigate("profile")
                                    // "home" não navega, já estamos nela
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.route) },
                            label = { Text(item.route.replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            },
            floatingActionButton = {
                androidx.compose.material3.FloatingActionButton(
                    onClick = { navController.navigate("addAccess") }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Cadastrar Senha")
                }
            },
            content = { innerPadding ->
                when (selectedItem) {
                    "scanner" -> {
                        QrCodeScannerScreen(
                            onQrCodeScanned = { qrValue ->
                                // Você pode salvar, navegar, ou processar o valor aqui.
                                println("QR Code lido: $qrValue")
                            }
                        )
                    }

                    "home" -> {
                        if (accessList.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Nenhum acesso cadastrado.")
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(innerPadding)
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

                    "profile" -> {
                        // Coloque aqui a tela de perfil ou outro conteúdo.
                        Box(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Perfil (em desenvolvimento)")
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun AccessCard(access: Access) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = access.nome.toString(), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = access.email.toString(), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = CryptoUtils.decrypt(access.senha.toString()),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

data class NavItem(val route: String, val icon: ImageVector)


