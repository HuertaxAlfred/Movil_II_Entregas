package api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import modelos.MovieResponse


class ApiClient(engine: HttpClientEngine) {
    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val BASE_URL = "https://api.themoviedb.org/3"
    private val API_KEY = "KeyKeyKeyKey"

    suspend fun getPopularMovies(): List<modelos.Pelicula> {
        val response: MovieResponse = client.get("$BASE_URL/movie/popular") {
            parameter("api_key", API_KEY)
            parameter("language", "es-ES")
            parameter("page", 1)
        }.body()
        return response.results
    }
}