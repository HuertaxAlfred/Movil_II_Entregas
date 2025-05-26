package modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pelicula(
    @SerialName("title") val titulo: String,
    @SerialName("poster_path") val ruta_poster: String? = null,
    @SerialName("original_title") val titulo_original: String,
    @SerialName("original_language") val idioma_original: String
)