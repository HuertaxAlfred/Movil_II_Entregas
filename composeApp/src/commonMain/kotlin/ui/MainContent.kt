package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import api.ApiClient
import io.ktor.client.engine.HttpClientEngine
import modelos.Pelicula
import ui.componentes.MovieList


@Composable
fun MainContent(engine: HttpClientEngine) {
    var movies by remember { mutableStateOf<List<Pelicula>>(emptyList()) }
    val apiClient = remember { ApiClient(engine) }

    LaunchedEffect(Unit) {
        movies = apiClient.getPopularMovies()
    }

    MovieList(movies)
}



