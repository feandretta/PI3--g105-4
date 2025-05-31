package projeto.integrador.utilities.funcs

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val PERMISSION_REQUEST_CODE = 101

// Função para verificar se a permissão foi concedida
fun checkPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_PHONE_STATE
    ) == PackageManager.PERMISSION_GRANTED
}

// Função para solicitar a permissão ao usuário (chame dentro de uma Activity)
fun requestPhoneStatePermission(activity: Activity) {
    if (!checkPermission(activity)) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            PERMISSION_REQUEST_CODE
        )
    }
}

// Objeto utilitário para obter o identificador do dispositivo
object DeviceUtils {

    @SuppressLint("HardwareIds", "MissingPermission")
    fun getDeviceImei(context: Context): String {
        return try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // A partir do Android 10, o IMEI não pode ser acessado diretamente
                getAndroidID(context)
            } else {
                if (checkPermission(context)) {
                    getDeviceImei(context)
                    telephonyManager.imei ?: getAndroidID(context)
                } else {
                    "PERMISSION_DENIED"
                }
            }
        } catch (e: SecurityException) {
            "PERMISSION_DENIED"
        } catch (e: Exception) {
            "UNKNOWN"
        }
    }

    fun getAndroidID(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}