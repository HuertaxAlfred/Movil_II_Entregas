package ui.componentes

//import ui.componentes.MovieImage


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import modelos.Pelicula


@Composable
fun MovieList(movies: List<Pelicula>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(movies) { movie ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    InfoRow(label = "Título:", value = movie.titulo)
                    InfoRow(label = "Idioma:", value = movie.idioma_original)
                    InfoRow(label = "Título original:", value = movie.titulo_original)
                    movie.ruta_poster?.let { ruta ->
//                        MovieImage(
//                            url = "https://image.tmdb.org/t/p/w500$ruta",
//                            modifier = Modifier
//                                .height(200.dp)
//                                .fillMaxWidth()
//                                .padding(top = 12.dp)
//                        )
                        MovieImage(
                            url = "https://image.tmdb.org/t/p/w500${movie.ruta_poster ?: ""}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)       // Altura un poco mayor para mejor visibilidad
                                .padding(top = 16.dp),
                            //.clip(RoundedCornerShape(12.dp)) // Bordes redondeados suaves
                            //.shadow(4.dp, RoundedCornerShape(12.dp)), // Sombra ligera para profundidad
                            title = movie.titulo
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}



