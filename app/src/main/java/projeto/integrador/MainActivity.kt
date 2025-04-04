package projeto.integrador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import projeto.integrador.presentation.theme.ui.ProjetoIntegrador1054Theme
import projeto.integrador.routes.NavigationSetup
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Toast
import projeto.integrador.data.funcs.requestPhoneStatePermission
import javax.crypto.Cipher
import javax.crypto.KeyGenerator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPhoneStatePermission(this)
        enableEdgeToEdge()
        setContent {
            ProjetoIntegrador1054Theme {
                val navController = rememberNavController()
                NavigationSetup(navController)
            }
        }
    }
}