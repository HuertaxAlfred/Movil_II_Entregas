package org.example.pelismultiplatform

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import io.ktor.client.engine.HttpClientEngine
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.MainContent


expect fun provideHttpClientEngine(): HttpClientEngine


@Composable
@Preview
fun App() {
    val engine = provideHttpClientEngine()

    MaterialTheme {
        Surface {
            MainContent(engine)
        }
    }
}