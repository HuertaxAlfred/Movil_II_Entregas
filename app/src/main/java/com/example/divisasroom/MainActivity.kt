package com.example.divisasroom

//import com.example.divisasroom.DAOS.CurrencyDao
//import com.example.divisasroom.DAOS.ExchangeRateUpdateDao
//import androidx.media3.common.util.Log
//import org.chromium.base.Log
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Observer
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.divisasroom.DAOS.ExchangeRateDao
import com.example.divisasroom.DB.AppDatabase
import com.example.divisasroom.WORKER.SyncExchangeRatesWorker
import com.example.divisasroom.ui.theme.DivisasRoomTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase
    private lateinit var exchangeRateUpdateDao: ExchangeRateDao
    private val loadingViewModel: LoadingViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar la base de datos dentro de onCreate
        db = AppDatabase.getDatabase(this)
        /**
         * Inicializar el DAO
         */
        exchangeRateUpdateDao = db.exchangeRateDao()

        /**
         * Llamada al worker para sincronizar cada hora, es el inicializador.
         */
        setupAutoSync()

        // Verificar el estado del Worker
        checkWorkerStatus()

        //  Consultar ContentProvider de manera segura
        //probarConsultaDAO() // Llamar la función de prueba

        // Llamar a la función para probar el ContentProvider
        //probarConsultaContentProvider(this, includeHours = true)

        //imprimirDatosDeLaBD() // Llamamos el método en segundo plano

        //probarConsultaDAO()
        //probarConsultaContentProvider(this)

        setContent {
            DivisasRoomTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopAppBar(title = { Text("Divisas API") }) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        // Mostrar barra de carga si WorkManager está ejecutando el Worker
                        if (loadingViewModel.isLoading.value) {
                            CircularProgressIndicator()
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text("Última sincronización completa!")
                                Button(onClick = { setupAutoSync() }) {
                                    Text("Iniciar Sincronización")
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    /**
     * Probar las consultas directo en el DAO
     */
    private fun probarConsultaDAO() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currencyCode = "USD" // 🔹 Moneda de prueba
                val startDateIso = "2025-03-12T04:00:00Z" // 🔹 Inicio en UTC
                val endDateIso = "2025-03-12T04:00:00Z"   // 🔹 Fin en UTC

                val resultados = exchangeRateUpdateDao.getRatesByCurrencyAndDateIso(
                    currencyCode, startDateIso, endDateIso
                )

                // 🔹 Imprimir los resultados en Logcat
                if (resultados.isNotEmpty()) {
                    resultados.forEach {
                        Log.d(
                            "DAO_TEST",
                            "Moneda: ${it.currency_code}, Tasa: ${it.rate}, Fecha: ${it.time_last_update_iso}"
                        )
                    }
                } else {
                    Log.d("DAO_TEST", "No se encontraron datos en el rango de fechas.")
                }
            } catch (e: Exception) {
                Log.e("DAO_TEST", "Error al consultar DAO: ${e.message}", e)
            }
        }
    }


    /**
     * Consultar el provider con una consulta de fechas.
     */
    private fun probarConsultaContentProvider(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val uri = Uri.parse("content://com.example.divisasroom.provider/exchange_rates")

            // Asegúrate de pasar los parámetros correctos para moneda y rango de fechas
            //2025-03-12 20:29:09.319 17263-17317 CONTENT_PROVIDERr       com.example.consumidor               D  Moneda: USD, Tasa: 0.04948686, Fecha: 2025-03-13T00:00:01Z

            val selectionArgs = arrayOf("USD", "2025-03-13T00:00:00Z", "2025-03-13T00:00:05Z")

            // Consulta al ContentProvider
            val cursor = context.contentResolver.query(uri, null, null, selectionArgs, null)

            cursor?.use {
                // Recorrer el cursor y obtener los resultados
                while (it.moveToNext()) {
                    val currency = it.getString(it.getColumnIndexOrThrow("currency_code"))
                    val rate = it.getDouble(it.getColumnIndexOrThrow("rate"))
                    val date = it.getString(it.getColumnIndexOrThrow("time_last_update_iso"))
                    Log.d("CONTENT_PROVIDER", "Moneda: $currency, Tasa: $rate, Fecha: $date")
                }
            } ?: Log.d("CONTENT_PROVIDER", "No se encontraron datos.")
        }
    }

    /**
     * Convierte un timestamp Unix a una fecha legible.
     */
    private fun convertUnixTimestampToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("America/Mexico_City") //Muestra en hora local
        return sdf.format(Date(timestamp * 1000))
    }

    /**
     * Imprimir todo el contenido de la base de datos
     */
    private fun imprimirDatosDeLaBD() {
        CoroutineScope(Dispatchers.IO).launch {  // 🔥 Ejecutar en un hilo de fondo
            val datos = exchangeRateUpdateDao.getAllRates()
            datos.forEach {
                val fechaLegible = convertUnixTimestampToDate(it.time_last_update_unix)
                Log.d(
                    "BD",
                    "Moneda: ${it.currency_code}, Tasa: ${it.rate}, Fecha Unix: ${it.time_last_update_unix}, Fecha Legible: $fechaLegible"
                )
            }
        }
    }

    /**
     * Configuracion del worker
     */
    fun setupAutoSync() {
        val syncRequest = PeriodicWorkRequestBuilder<SyncExchangeRatesWorker>(1, TimeUnit.HOURS)
            .setInitialDelay(0, TimeUnit.SECONDS)
            .addTag("syncExchangeRatesTag")  // Usamos el mismo tag para rastrear el Worker
            .build()
        WorkManager.getInstance(applicationContext).enqueue(syncRequest)
    }

    //Revisar el estado en el que esta el worker.
    fun checkWorkerStatus() {
        WorkManager.getInstance(applicationContext)
            .getWorkInfosByTagLiveData("syncExchangeRatesTag") // Usamos un tag para identificar el Worker
            .observe(this, Observer { workInfoList ->
                // Aquí puedes verificar el estado del trabajo
                for (workInfo in workInfoList) {
                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED -> Log.d("MainActivity", "El Worker esta en cola.")
                        WorkInfo.State.RUNNING -> Log.d(
                            "MainActivity",
                            "El Worker esta en ejecucion."
                        )

                        WorkInfo.State.SUCCEEDED -> Log.d(
                            "MainActivity",
                            "El Worker se completo exitosamente."
                        )

                        WorkInfo.State.FAILED -> Log.d("MainActivity", "El Worker fallo.")
                        WorkInfo.State.CANCELLED -> Log.d(
                            "MainActivity",
                            "El Worker fue cancelado."
                        )

                        WorkInfo.State.BLOCKED -> Log.d("MainActivity", "El Worker esta bloqueado.")
                        else -> Log.d(
                            "MainActivity",
                            "Estado desconocido del Worker: ${workInfo.state}"
                        )
                    }
                }
            })
    }
}

//Componentes que estaban por defecto.
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Proyecto: $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DivisasRoomTheme {
        Greeting("DIVISAS API")
    }
}