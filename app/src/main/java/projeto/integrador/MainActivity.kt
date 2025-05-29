package projeto.integrador

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import projeto.integrador.presentation.theme.ui.ProjetoIntegrador1054Theme
import projeto.integrador.routes.NavigationSetup
import projeto.integrador.utilities.funcs.CryptoUtils
import projeto.integrador.utilities.funcs.requestPhoneStatePermission

class MainActivity : ComponentActivity() {

    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE
    )

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CryptoUtils.init(this)

        // Primeiro: pedir permissões
        checkPermissions()

        // Se você quiser pedir manualmente a PHONE_STATE, pode manter
        requestPhoneStatePermission(this)

        // Ativar o comportamento de Edge-to-Edge
        enableEdgeToEdge()

        // Agora monta a tela
        setContent {
            ProjetoIntegrador1054Theme {
                val navController = rememberNavController()
                NavigationSetup(navController)
            }
        }
    }

    private fun checkPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 0)
        }
    }
}
