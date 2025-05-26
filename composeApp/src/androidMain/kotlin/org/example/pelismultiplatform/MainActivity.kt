package org.example.pelismultiplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ui.MainContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val engine = io.ktor.client.engine.okhttp.OkHttp.create()

        setContent {
            //App()
            MainContent(engine)

        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}