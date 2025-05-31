package projeto.integrador.ui.screens.cadastro

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import projeto.integrador.ui.screens.addAccess.AddAccessViewModel
import projeto.integrador.ui.screens.components.RequiredTextField

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccessScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: AddAccessViewModel = remember { AddAccessViewModel() }
) {
    val scrollState = rememberScrollState()
    val isFormValid = viewModel.isFormValid()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Senha") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RequiredTextField(
                value = viewModel.nome.value,
                onValueChange = { viewModel.nome.value = it },
                label = "Nome",
                modifier = Modifier.fillMaxWidth()
            )

            //menu dropdown no formato do texfield mostrando as categoriass

            RequiredTextField(
                value = viewModel.url.value,
                onValueChange = { viewModel.url.value = it },
                label = "URL",
                modifier = Modifier.fillMaxWidth()
            )

            RequiredTextField(
                value = viewModel.email.value,
                onValueChange = { viewModel.email.value = it },
                label = "Email",
                modifier = Modifier.fillMaxWidth()
            )

            RequiredTextField(
                value = viewModel.senha.value,
                onValueChange = { viewModel.senha.value = it },
                label = "Senha",
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.descricao.value,
                onValueChange = { viewModel.descricao.value = it },
                label = { Text("Descrição") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.salvar() },
                enabled = isFormValid,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Salvar")
            }
        }
    }
}
