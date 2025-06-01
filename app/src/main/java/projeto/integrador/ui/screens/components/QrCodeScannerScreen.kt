package projeto.integrador.ui.screens.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import androidx.camera.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import java.util.concurrent.Executors
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import projeto.integrador.utilities.loginQrCode

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun QrCodeScannerScreen(
    padding: PaddingValues
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }  // Cria o visual onde a câmera vai ser exibida
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() } //Para processar as imagens da camera

    // Variáveis para controlar se já escaneou e se o alerta está visível
    var scanned by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        // Mostra a imagem da câmera na tela
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        // Define que só vamos escanear QR Codes
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        val scanner = BarcodeScanning.getClient(options)   // Inicia o leitor de QR Code

        // Configura o sistema para analisar as imagens da câmera em tempo real
        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null && !scanned) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)  // Converte a imagem da câmera em um formato aceito pelo ML Kit
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        barcodes.firstOrNull()?.rawValue?.let { rawValue ->  // Pega o primeiro QR Code encontrado, se tiver algum
                            scanned = true  // Marca que já foi lido
                            loginQrCode(rawValue) { message ->
                                dialogMessage = message
                                showDialog = true
                            }
                        }
                    }
                    .addOnCompleteListener {
                        imageProxy.close() // Libera a imagem para a próxima leitura
                    }
            } else {
                imageProxy.close()
            }
        }
// Escolhe a câmera traseira
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, analysis)
    }
    // Parte visual da tela e mostra o video da camera
    Box(modifier = Modifier.padding(padding).fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
//desenha o quadrado para centralizar qrcode
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(250.dp)
                .border(2.dp, Color.White, shape = RoundedCornerShape(8.dp))
        )
//mostra alerta quando le qrcode
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("QR Code") },
                text = { Text(dialogMessage) },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        scanned = false  //permite escanear outro qrcode
                    }) {
                        Text("Ler outro")
                    }
                }
            )
        }
    }
}