package com.example.futbolito

import SensorViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.futbolito.ui.theme.FutbolitoTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FutbolitoTheme {
                val viewModel: SensorViewModel = viewModel()
                PocketSoccerGame(sensorViewModel = viewModel)
            }
        }
    }
}

@Composable
fun PocketSoccerGame(sensorViewModel: SensorViewModel = viewModel()) {
    //canvasSize: almacenar el tamaño del área de juego.
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    //Calcula el radio de la pelota basado en el tamaño de la pantalla.
    val ballRadius by remember(canvasSize) {
        mutableStateOf(minOf(canvasSize.width, canvasSize.height) * 0.05f)
    }
    //Calcular las dimenciones de las porterias
    val goalWidth by remember(canvasSize) {
        mutableStateOf(canvasSize.width * 0.4f) // 40% del ancho del canvas
    }
    val goalHeight by remember(canvasSize) {
        mutableStateOf(minOf(canvasSize.width, canvasSize.height) * 0.05f)
    }

    // Posición inicial de la pelota CENTRADA
    var ballPosition by remember(canvasSize) {
        mutableStateOf(
            if (canvasSize == Size.Zero) Offset.Zero
            else Offset(canvasSize.width / 2, canvasSize.height / 2)
        )
    }/*
    * velocityX y velocityY manejan la velocidad de la pelota.
    * scoreTop y scoreBottom almacenan los puntajes.
    */
    var velocityX by remember { mutableStateOf(0f) }
    var velocityY by remember { mutableStateOf(0f) }
    var scoreTop by remember { mutableStateOf(0) }
    var scoreBottom by remember { mutableStateOf(0) }


    /*
    * Se crean dos porterías (goalTopRect y goalBottomRect) como rectángulos (Rect).
    * Ambas porterías están centradas horizontalmente.
    * Se usan remember y mutableStateOf para evitar recalculaciones innecesarias.
    */
    val goalTopRect by remember(canvasSize, goalWidth, goalHeight) {
        mutableStateOf(
            Rect(
                Offset((canvasSize.width - goalWidth) / 2, 0f), Size(goalWidth, goalHeight)
            )
        )
    }
    val goalBottomRect by remember(canvasSize, goalWidth, goalHeight) {
        mutableStateOf(
            Rect(
                Offset((canvasSize.width - goalWidth) / 2, canvasSize.height - goalHeight),
                Size(goalWidth, goalHeight)
            )
        )
    }


    /*
    * Comienzan los rebotes
    * ada vez que cambian los valores del sensor, se actualiza la velocidad de la pelota.
    * -accel.x mueve la pelota horizontalmente, accel.y la mueve verticalmente.
    * */
    LaunchedEffect(sensorViewModel.sensorValues) {
        if (canvasSize == Size.Zero) return@LaunchedEffect

        val accel = sensorViewModel.sensorValues
        velocityX += -accel.x * 0.5f
        velocityY += accel.y * 0.5f

        var newX = ballPosition.x + velocityX
        var newY = ballPosition.y + velocityY

        /*
        *
        * Aquí se revisa si la pelota toca el borde izquierdo (newX - ballRadius <= 0) o derecho (newX + ballRadius >= canvasSize.width).
        * Si es así, la velocidad en el eje X se invierte (-velocityX) y se le aplica un factor de fricción del 80% (* 0.8f) para que el rebote pierda fuerza.
        * Luego se ajusta la posición con coerceIn para evitar que la pelota se salga del campo.
        *
        * */

        // Límites horizontales - Si la pelota toca los bordes laterales, rebota.
        if (newX - ballRadius <= 0 || newX + ballRadius >= canvasSize.width) {
            velocityX = -velocityX * 0.8f
            newX = newX.coerceIn(ballRadius, canvasSize.width - ballRadius)
        }

        /*
        * Si la pelota llega al borde superior, revisa si está dentro de la portería:
        * Si NO está en la portería, rebota.
        * Si está en la portería, se anota un gol para el jugador de abajo.
        * Lo mismo se hace para la portería inferior.
        */
        if (newY - ballRadius <= 0) {
            if (newX !in goalTopRect.left..goalTopRect.right) {
                velocityY = -velocityY * 0.8f
                newY = ballRadius
            } else {
                scoreBottom += 1
                newX = canvasSize.width / 2
                newY = canvasSize.height / 2
                velocityX = 0f
                velocityY = 0f
            }
        }
        // Portería inferior
        if (newY + ballRadius >= canvasSize.height) {
            if (newX !in goalBottomRect.left..goalBottomRect.right) {
                velocityY = -velocityY * 0.8f
                newY = canvasSize.height - ballRadius
            } else {
                scoreTop += 1
                newX = canvasSize.width / 2
                newY = canvasSize.height / 2
                velocityX = 0f
                velocityY = 0f
            }
        }
        ballPosition = Offset(newX, newY)
    }
    //Terminan los rebotes

    //Dibujamos el juego en si
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            //Contadores de goles en las porterias superior e inferior
            text = "\nP Arriba: $scoreTop - P Abajo: $scoreBottom",
            fontSize = 24.sp,
            color = Color.Blue,
            modifier = Modifier.padding(16.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            //Canva ocupara todo el tamaño de la caja padre este caso BOX.
            Canvas(modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    canvasSize = Size(
                        coordinates.size.width.toFloat(), coordinates.size.height.toFloat()
                    )
                    // Reiniciamos la posición de la pelota cuando cambia el tamaño
                    ballPosition = Offset(canvasSize.width / 2, canvasSize.height / 2)
                }) {
                // Dibujamos el campo (fondo verde)
                drawRect(Color(0xFF35682d), size = canvasSize)
                // Dibujamos las porterías
                drawRect(Color.White, topLeft = goalTopRect.topLeft, size = goalTopRect.size)
                drawRect(Color.White, topLeft = goalBottomRect.topLeft, size = goalBottomRect.size)
                // Dibujamos la pelota
                drawCircle(Color.Black, radius = ballRadius, center = ballPosition)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FutbolitoTheme {
        Greeting("Android")
    }
}