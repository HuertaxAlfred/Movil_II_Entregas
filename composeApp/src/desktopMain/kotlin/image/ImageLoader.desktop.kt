package image

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image

private val client = HttpClient(CIO)

actual suspend fun loadImage(url: String, context: Any?): ImageBitmap? =
    withContext(Dispatchers.IO) {
        try {
            val byteArray: ByteArray = client.get(url).body()
            val skiaImage: Image = Image.makeFromEncoded(byteArray)
            skiaImage.toComposeImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
