package org.example.pelismultiplatform

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js
import kotlinx.browser.document

//actual fun provideHttpClientEngine(): HttpClientEngine = Js.create()
actual fun provideHttpClientEngine(): HttpClientEngine = Js.create()


//@OptIn(ExperimentalComposeUiApi::class)
//fun main() {
//    ComposeViewport(document.body!!) {
//        App()
//    }
//}


//@OptIn(ExperimentalComposeUiApi::class)
//fun main() {
//    val root = document.getElementById("app-root") ?: error("No se encontró #app-root")
//    ComposeViewport(root) {
//        App()
//    }
//}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val canvasRoot =
        document.getElementById("canvas-container") ?: error("No se encontró #canvas-container")
    ComposeViewport(canvasRoot) {
        App()
    }
}
