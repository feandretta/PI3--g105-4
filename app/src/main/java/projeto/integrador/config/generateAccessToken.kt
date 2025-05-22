package projeto.integrador.config

import android.os.Build
import android.util.Base64;
import android.util.Base64.DEFAULT
import androidx.annotation.RequiresApi
import java.util.random.RandomGenerator

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
fun generateAccessToken() : String{

    val generator : RandomGenerator = RandomGenerator.getDefault()

    val stream = generator.longs(256)
    val byteStream = stream.toString().toByteArray()

    val byteEncode = Base64.encode(byteStream, DEFAULT)

    return byteEncode.toString()


}

