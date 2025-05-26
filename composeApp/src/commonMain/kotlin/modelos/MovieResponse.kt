package modelos

import kotlinx.serialization.Serializable

@Serializable
data class MovieResponse(val results: List<Pelicula>)