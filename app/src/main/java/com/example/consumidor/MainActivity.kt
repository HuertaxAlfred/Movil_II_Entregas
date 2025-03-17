package com.example.consumidor

import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.consumidor.ui.theme.ConsumidorTheme
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ConsumidorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        InterfazPrincipalConGrafica()
                    }
                }

            }
        }
    }

    /*
    * Consumir manualmente el provider
    */
    private fun probarConsultaContentProvider(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val uri = Uri.parse("content://com.example.divisasroom.provider/exchange_rates")
            //val uri = Uri.parse("content://com.example.divisasroom.provider/exchange_rates")

            // Parámetros de selección para la consulta
            val selectionArgs = arrayOf("USD", "2025-03-13T00:00:00Z", "2025-03-13T00:00:05Z")
            //val selectionArgs = arrayOf("MXN", "2025-03-13T00:00:00Z", "2025-03-13T00:00:05Z")

            // Consulta al ContentProvider
            val cursor = context.contentResolver.query(uri, null, null, selectionArgs, null)

            cursor?.use {
                if (it.count > 0) {
                    // Recorrer el cursor y obtener los resultados
                    while (it.moveToNext()) {
                        val currency = it.getString(it.getColumnIndexOrThrow("currency_code"))
                        val rate = it.getDouble(it.getColumnIndexOrThrow("rate"))
                        val date = it.getString(it.getColumnIndexOrThrow("time_last_update_iso"))
                        Log.d("CONTENT_PROVIDERr", "Moneda: $currency, Tasa: $rate, Fecha: $date")
                    }
                } else {
                    Log.d("CONTENT_PROVIDER", "No se encontraron datos.")
                }
            } ?: run {
                Log.d("CONTENT_PROVIDER", "Error al consultar el ContentProvider.")
            }
        }
    }


}


val currencyNames = mapOf(
    "USD" to "Dólar Estadounidense",
    "AED" to "Dirham de los Emiratos Árabes Unidos",
    "AFN" to "Afgani Afgano",
    "ALL" to "Lek Albanés",
    "AMD" to "Dram Armenio",
    "ANG" to "Florín de las Antillas Neerlandesas",
    "AOA" to "Kwanza Angoleño",
    "ARS" to "Peso Argentino",
    "AUD" to "Dólar Australiano",
    "AWG" to "Florín Arubeño",
    "AZN" to "Manat Azerbaiyano",
    "BAM" to "Marco Convertible",
    "BBD" to "Dólar de Barbados",
    "BDT" to "Taka Bangladesí",
    "BGN" to "Lev Búlgaro",
    "BHD" to "Dinar Bahreiní",
    "BIF" to "Franco Burundés",
    "BMD" to "Dólar Bermudeño",
    "BND" to "Dólar Bruneano",
    "BOB" to "Boliviano",
    "BRL" to "Real Brasileño",
    "BSD" to "Dólar de las Bahamas",
    "BTN" to "Ngultrum Butanés",
    "BWP" to "Pula Botsuano",
    "BYN" to "Rublo Bielorruso",
    "BZD" to "Dólar Beliceño",
    "CAD" to "Dólar Canadiense",
    "CDF" to "Franco Congoleño",
    "CHF" to "Franco Suizo",
    "CLP" to "Peso Chileno",
    "CNY" to "Yuan Chino",
    "COP" to "Peso Colombiano",
    "CRC" to "Colón Costarricense",
    "CUP" to "Peso Cubano",
    "CVE" to "Escudo Caboverdiano",
    "CZK" to "Corona Checa",
    "DJF" to "Franco Yibutiano",
    "DKK" to "Corona Danesa",
    "DOP" to "Peso Dominicano",
    "DZD" to "Dinar Argelino",
    "EGP" to "Libra Egipcia",
    "ERN" to "Nakfa Eritreo",
    "ETB" to "Birr Etíope",
    "EUR" to "Euro",
    "FJD" to "Dólar Fijiano",
    "FKP" to "Libra Malvinense",
    "FOK" to "Corona de las Islas Feroe",
    "GBP" to "Libra Esterlina",
    "GEL" to "Lari Georgiano",
    "GGP" to "Libra de Guernsey",
    "GHS" to "Cedi Ghanés",
    "GIP" to "Libra de Gibraltar",
    "GMD" to "Dalasi Gambiano",
    "GNF" to "Franco Guineano",
    "GTQ" to "Quetzal Guatemalteco",
    "GYD" to "Dólar Guyanés",
    "HKD" to "Dólar de Hong Kong",
    "HNL" to "Lempira Hondureño",
    "HRK" to "Kuna Croata",
    "HTG" to "Gourde Haitiano",
    "HUF" to "Forinto Húngaro",
    "IDR" to "Rupia Indonesia",
    "ILS" to "Nuevo Shekel Israelí",
    "IMP" to "Libra de la Isla de Man",
    "INR" to "Rupia India",
    "IQD" to "Dinar Iraquí",
    "IRR" to "Rial Iraní",
    "ISK" to "Corona Islandesa",
    "JEP" to "Libra de Jersey",
    "JMD" to "Dólar Jamaiquino",
    "JOD" to "Dinar Jordano",
    "JPY" to "Yen Japonés",
    "KES" to "Chelín Keniano",
    "KGS" to "Som Kirguís",
    "KHR" to "Riel Camboyano",
    "KID" to "Dólar de Kiribati",
    "KMF" to "Franco Comorense",
    "KRW" to "Won Surcoreano",
    "KWD" to "Dinar Kuwaiti",
    "KYD" to "Dólar de las Islas Caimán",
    "KZT" to "Tenge Kazajo",
    "LAK" to "Kip Lao",
    "LBP" to "Libra Libanesa",
    "LKR" to "Rupia de Sri Lanka",
    "LRD" to "Dólar Liberiano",
    "LSL" to "Loti Lesotense",
    "LYD" to "Dinar Libio",
    "MAD" to "Dirham Marroquí",
    "MDL" to "Leu Moldavo",
    "MGA" to "Ariary Malgache",
    "MKD" to "Denar Macedonio",
    "MMK" to "Kyat Myanmar",
    "MNT" to "Tugrik Mongol",
    "MOP" to "Pataca Macaense",
    "MRU" to "Ouguiya Mauritano",
    "MUR" to "Rupia Mauricio",
    "MVR" to "Rupia Maldiva",
    "MWK" to "Kwacha Malauí",
    "MXN" to "Peso Mexicano",
    "MYR" to "Ringgit Malayo",
    "MZN" to "Metical Mozambicano",
    "NAD" to "Dólar Namibio",
    "NGN" to "Naira Nigeriana",
    "NIO" to "Córdoba Nicaragüense",
    "NOK" to "Corona Noruega",
    "NPR" to "Rupia Nepalesa",
    "NZD" to "Dólar Neozelandés",
    "OMR" to "Rial Omaní",
    "PAB" to "Balboa Panameño",
    "PEN" to "Nuevo Sol Peruano",
    "PGK" to "Kina Papú de Nueva Guinea",
    "PHP" to "Peso Filipino",
    "PKR" to "Rupia Pakistaní",
    "PLN" to "Zloty Polaco",
    "PYG" to "Guaraní Paraguayo",
    "QAR" to "Riyal Catarí",
    "RON" to "Leu Rumano",
    "RSD" to "Dinar Serbio",
    "RUB" to "Rublo Ruso",
    "RWF" to "Franco Ruandés",
    "SAR" to "Riyal Saudí",
    "SBD" to "Dólar Salomónico",
    "SCR" to "Rupia de Seychelles",
    "SDG" to "Libra Sudanesa",
    "SEK" to "Corona Sueca",
    "SGD" to "Dólar Singapurense",
    "SHP" to "Libra Santahelena",
    "SLE" to "Leone Sierra Leona",
    "SLL" to "Leone de Sierra Leona",
    "SOS" to "Chelín Somalí",
    "SRD" to "Dólar Surinamés",
    "SSP" to "Libra del Sur de Sudán",
    "STN" to "Dobra Santo Tomé y Príncipe",
    "SYP" to "Libra Siria",
    "SZL" to "Lilangeni Suazi",
    "THB" to "Baht Tailandés",
    "TJS" to "Somoni Tayiko",
    "TMT" to "Manat Turcomano",
    "TND" to "Dinar Tunecino",
    "TOP" to "Paʻanga Tonga",
    "TRY" to "Lira Turca",
    "TTD" to "Dólar Trinidadense",
    "TVD" to "Dólar Tuvaluano",
    "TWD" to "Nuevo Dollar Taiwanés",
    "TZS" to "Chelín Tanzano",
    "UAH" to "Grivna Ucraniana",
    "UGX" to "Chelín Ugandés",
    "UYU" to "Peso Uruguayo",
    "UZS" to "Som Uzbeko",
    "VES" to "Bolívar Venezolano",
    "VND" to "Dong Vietnamita",
    "VUV" to "Vatu Vanuatense",
    "WST" to "Tala Samoana",
    "XAF" to "Franco CFA de África Central",
    "XCD" to "Dólar del Caribe Oriental",
    "XDR" to "Derechos Especiales de Giro",
    "XOF" to "Franco CFA de África Occidental",
    "XPF" to "Franco CFP",
    "YER" to "Rial Yemení",
    "ZAR" to "Rand Sudafricano",
    "ZMW" to "Kwacha Zambiano",
    "ZWL" to "Dólar Zimbabuense"
)

@Composable
fun InterfazPrincipalConGrafica() {
    val context = LocalContext.current

    var date1 by remember { mutableStateOf("") }
    var date2 by remember { mutableStateOf("") }
    var time1 by remember { mutableStateOf("") }
    var time2 by remember { mutableStateOf("") }
    var currencyCode by remember { mutableStateOf("") } // Almacenar la clave de la divisa
    var exchangeRates by remember { mutableStateOf<List<Pair<String, Float>>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {

        CurrencyTickerBanner(currencyNames) // ✅ Se muestra en la parte superior
        Spacer(modifier = Modifier.height(8.dp)) // Espaciado entre banner y contenido

        // Cajita de texto para ingresar la clave de la divisa
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = currencyCode,
                onValueChange = { currencyCode = it }, // Actualiza el valor de currencyCode
                label = { Text("Clave de la divisa (ej. USD)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Selección de Fecha 1
        Row(modifier = Modifier.fillMaxWidth()) {
            SmallButton(text = "Fecha de INICIO") {
                showDatePicker(context) { selectedDate ->
                    date1 = selectedDate
                }
            }
            Spacer(modifier = Modifier.width(8.dp))  // Espaciado entre botones
            Text(text = date1, modifier = Modifier.align(Alignment.CenterVertically))
        }

        // Selección de Hora 1
        Row(modifier = Modifier.fillMaxWidth()) {
            SmallButton(text = "Hora INICIO") {
                showTimePicker(context) { selectedTime ->
                    time1 = selectedTime
                }
            }
            Spacer(modifier = Modifier.width(8.dp))  // Espaciado entre botones
            Text(text = time1, modifier = Modifier.align(Alignment.CenterVertically))
        }

        // Selección de Fecha 2
        Row(modifier = Modifier.fillMaxWidth()) {
            SmallButton(text = "Fecha FINALIZACIÓN") {
                showDatePicker(context) { selectedDate ->
                    date2 = selectedDate
                }
            }
            Spacer(modifier = Modifier.width(8.dp))  // Espaciado entre botones
            Text(text = date2, modifier = Modifier.align(Alignment.CenterVertically))
        }

        // Selección de Hora 2
        Row(modifier = Modifier.fillMaxWidth()) {
            SmallButton(text = "Hora FINALIZACIÓN") {
                showTimePicker(context) { selectedTime ->
                    time2 = selectedTime
                }
            }
            Spacer(modifier = Modifier.width(8.dp))  // Espaciado entre botones
            Text(text = time2, modifier = Modifier.align(Alignment.CenterVertically))
        }
        Button(
            onClick = {
                ConsumirDatosProvider(context, date1, time1, date2, time2, currencyCode) { fetchedData ->
                    exchangeRates = fetchedData
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Consultar")
        }
        //Aquí agregamos la condición para mostrar la gráfica solo si hay datos
        if (exchangeRates.isNotEmpty()) {
            Toast.makeText(context, "¡Tus divisas fueron graficadas!", Toast.LENGTH_SHORT).show()
            LineChartView(exchangeRates)
        }
    }
}

fun ConsumirDatosProvider(
    context: Context,
    date1: String,
    time1: String,
    date2: String,
    time2: String,
    currencyCode: String,
    onDataFetched: (List<Pair<String, Float>>) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        val uri = Uri.parse("content://com.example.divisasroom.provider/exchange_rates")

        val startDateTime = "$date1 $time1"
        val endDateTime = "$date2 $time2"

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val startDateFormatted = LocalDateTime.parse(startDateTime, formatter)
            .atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME)
        val endDateFormatted = LocalDateTime.parse(endDateTime, formatter)
            .atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME)

        val selectionArgs = arrayOf(currencyCode, startDateFormatted, endDateFormatted)
        val cursor = context.contentResolver.query(uri, null, null, selectionArgs, null)

        val ratesList = mutableListOf<Pair<String, Float>>()

        cursor?.use {
            while (it.moveToNext()) {
                val date = it.getString(it.getColumnIndexOrThrow("time_last_update_iso"))
                val rate = it.getDouble(it.getColumnIndexOrThrow("rate")).toFloat()
                ratesList.add(Pair(date, rate))
            }
        }
        cursor?.close()
        onDataFetched(ratesList)
    }
}

@Composable
fun LineChartView(exchangeRates: List<Pair<String, Float>>) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                axisRight.isEnabled = false
                legend.isEnabled = true
            }
        },
        update = { chart ->
            val entries = exchangeRates.mapIndexed { index, data ->
                Entry(index.toFloat(), data.second)  // Aquí usamos el índice como referencia
            }

            val dataSet = LineDataSet(entries, "Tasa de cambio").apply {
                color = AndroidColor.BLUE
                valueTextColor = AndroidColor.BLACK
                lineWidth = 2f
                setCircleColor(AndroidColor.RED)
                setDrawValues(true)
            }

            // Agregar el ValueFormatter para mostrar fechas en el eje X
            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return if (index in exchangeRates.indices) exchangeRates[index].first else ""
                }
            }
            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}

fun probarConsultaContentProvider(
    context: Context,
    date1: String,
    time1: String,
    date2: String,
    time2: String,
    currencyCode: String
) {
    CoroutineScope(Dispatchers.IO).launch {
        val uri = Uri.parse("content://com.example.divisasroom.provider/exchange_rates")

        // Convertir las fechas y horas a formato ISO 8601
        val startDateTime = "${date1} ${time1}"
        val endDateTime = "${date2} ${time2}"

        // Formatear a la fecha ISO 8601
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val startDateFormatted =
            LocalDateTime.parse(startDateTime, formatter).atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_DATE_TIME)
        val endDateFormatted = LocalDateTime.parse(endDateTime, formatter).atOffset(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_DATE_TIME)

        // Mostrar los datos recolectados
        Log.d(
            "CONTENT_PROVIDER",
            "Datos recolectados: Fecha 1: $startDateFormatted, Hora 1: $time1, Fecha 2: $endDateFormatted, Hora 2: $time2"
        )

        // Ajustar los parámetros para la consulta
        val selectionArgs = arrayOf(currencyCode, startDateFormatted, endDateFormatted)

        // Consulta al ContentProvider
        val cursor = context.contentResolver.query(uri, null, null, selectionArgs, null)

        cursor?.use {
            if (it.count > 0) {
                // Recorrer el cursor y obtener los resultados
                while (it.moveToNext()) {
                    val currency = it.getString(it.getColumnIndexOrThrow("currency_code"))
                    val rate = it.getDouble(it.getColumnIndexOrThrow("rate"))
                    val date = it.getString(it.getColumnIndexOrThrow("time_last_update_iso"))

                    // Mostrar los resultados obtenidos de la consulta
                    Log.d("CONTENT_PROVIDER", "Moneda: $currency, Tasa: $rate, Fecha: $date")
                }
            } else {
                Log.d("CONTENT_PROVIDER", "No se encontraron datos.")
            }
        } ?: run {
            Log.d("CONTENT_PROVIDER", "Error al consultar el ContentProvider.")
        }
    }
}


@Composable
fun SmallButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            //.weight(1f)  // Distribuir espacio
            .height(48.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, fontSize = 14.sp)
    }
}

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hour, minute ->
            onTimeSelected(String.format("%02d:%02d", hour, minute))
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    ).show()
}

@Composable
fun CurrencyTickerBanner(currencyNames: Map<String, String>) {
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        while (true) {
            delay(0) // Reducir el tiempo de delay para que el movimiento sea más rápido (ajústalo a tu gusto)

            val maxScroll = scrollState.maxValue
            val nextScroll = (scrollState.value + 7).coerceAtMost(maxScroll) // Desplazamiento rápido pero legible (ajusta el número si es necesario)

            // Desplazamiento suave con una animación rápida
            scrollState.animateScrollTo(nextScroll, animationSpec = tween(1, easing = LinearEasing)) // Animación rápida (200ms)

            // Reiniciar cuando se alcanza el final
            if (nextScroll >= maxScroll) {
                // Esperar un poco antes de reiniciar para evitar un "salto" abrupto
                delay(1000) // Ajusta este valor si es necesario
                scrollState.scrollTo(0) // Reiniciar animación
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.Black), // Color negro usando Jetpack Compose
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState, false)
                .padding(horizontal = 8.dp)
        ) {
            currencyNames.forEach { (code, currencyNames) ->  // Eliminado el rate
                val currencyName = currencyNames ?: code // Usa el nombre si existe, sino el código
                Text(
                    text = " $code ($currencyName) |",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ConsumidorTheme {
        Greeting("Android")
        InterfazPrincipalConGrafica()
    }
}