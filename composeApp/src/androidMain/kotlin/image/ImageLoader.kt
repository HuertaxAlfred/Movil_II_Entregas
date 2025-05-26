// androidMain/image/ImageLoader.kt
package image

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun loadImage(url: String, context: Any?): ImageBitmap? {
    val ctx = context as? Context ?: return null
    return withContext(Dispatchers.IO) {
        try {
            val loader = ImageLoader(ctx)
            val request = ImageRequest.Builder(ctx)
                .data(url)
                .build()

            val result = loader.execute(request)
            if (result is SuccessResult) {
                result.drawable.toBitmap().asImageBitmap()
            } else null
        } catch (e: Exception) {
            println("❌ Error al cargar imagen: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
