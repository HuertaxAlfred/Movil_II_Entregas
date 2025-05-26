package org.example.pelismultiplatform

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO


actual fun provideHttpClientEngine(): HttpClientEngine = CIO.create()

fun main() = application {

    val engine = io.ktor.client.engine.cio.CIO.create()

    Window(onCloseRequest = ::exitApplication, title = "Peliculas Top") {
        App()
    }


//    Window(
//        onCloseRequest = ::exitApplication,
//        title = "pelismultiplatform",
//    ) {
//        App()
//    }
}