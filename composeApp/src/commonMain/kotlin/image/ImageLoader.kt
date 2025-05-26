// commonMain/image/ImageLoader.kt
package image

import androidx.compose.ui.graphics.ImageBitmap

expect suspend fun loadImage(url: String, context: Any? = null): ImageBitmap?
