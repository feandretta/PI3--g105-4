package projeto.integrador.ui.screens.unlock

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import projeto.integrador.R

@Composable
fun UnlockScreen(
    context: Context,
    navController: NavHostController,
    viewModel: UnlockViewModel = viewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )

            Text(
                text = "Super ID",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedSecureTextField(
                state = viewModel.senhaState,
                label = { Text("Senha") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textObfuscationMode = if (viewModel.senhaVisivel.value) {
                    TextObfuscationMode.Visible
                } else {
                    TextObfuscationMode.RevealLastTyped
                },
                trailingIcon = {
                    val icon = if (viewModel.senhaVisivel.value) {
                        Icons.Default.Visibility
                    } else {
                        Icons.Default.VisibilityOff
                    }
                    IconButton(onClick = { viewModel.toggleSenhaVisivel() }) {
                        Icon(icon, contentDescription = null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = {
                    viewModel.unlock(context) { success ->
                        if (success) {
                            navController.navigate("home")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Desbloquear")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Deseja trocar de conta? ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "Clique aqui",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.clickable {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("signIn")
                    }
                )
            }
        }
    }
}
