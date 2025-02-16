package com.example.divisasroom

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Observer
import com.example.divisasroom.ui.theme.DivisasRoomTheme
import com.example.divisasroom.DAOS.CurrencyDao
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.divisasroom.DAOS.ExchangeRateUpdateDao
import com.example.divisasroom.DB.DBPruebas
import com.example.divisasroom.WORKER.SyncExchangeRatesWorker
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    private lateinit var db: DBPruebas
    private lateinit var currencyDao: CurrencyDao
    private lateinit var exchangeRateUpdateDao: ExchangeRateUpdateDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar la base de datos dentro de onCreate
        db = DBPruebas.getDatabase(this)
        currencyDao = db.currencyDao()
        exchangeRateUpdateDao = db.exchangeRateUpdateDao()  // Inicialización del ExchangeRateUpdateDao

        // Configuración del Worker para sincronizar cada hora
        setupAutoSync()

        // Verificar el estado del Worker
        checkWorkerStatus()

//        // Inicializar la base de datos dentro de onCreate
//        db = DBPruebas.getDatabase(this)
//        currencyDao = db.currencyDao()
//        exchangeRateUpdateDao = db.exchangeRateUpdateDao()  // Inicialización del ExchangeRateUpdateDao
//
//
//        // Llamada a la función que obtiene las divisas
//        syncExchangeRates()

        setContent {
            DivisasRoomTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
//        // Insertar una divisa de ejemplo (se ejecuta en un hilo de fondo con Coroutines)
//        lifecycleScope.launch {
//            val sampleCurrency = Currency(code = "USD", rate = 7.0)
//            currencyDao.insertCurrencies(listOf(sampleCurrency))
//
//            // Obtener todas las divisas guardadas y mostrarlas en el Log
//            val currencies = currencyDao.getAllCurrencies()
//            currencies.forEach {
//                Log.d("MainActivity", "Divisa: ${it.code}, Tasa: ${it.rate}")
//            }
//        }
    }

//    private fun syncExchangeRates() {
//        lifecycleScope.launch {
//            try {
//                val apiKey = "9af70ebaf64e2aabfb1e8f2b"
//                val response = RetrofitInstance.api.getExchangeRates(apiKey)
//
//                Log.d("MainActivity", "Respuesta completa: $response")
//
//                // Verificar si conversion_rates está vacío o nulo
//                if (response.conversion_rates.isNullOrEmpty()) {
//                    Log.e("MainActivity", "Rates es nulo o vacío.")
//                } else {
//                    // Si hay datos en conversion_rates, procesarlos
//                    val currencies = response.conversion_rates.map { (code, rate) ->
//                        Currency(code = code, rate = rate)
//                    }
//
//                    // Insertar las divisas en la base de datos
//                    currencyDao.insertCurrencies(currencies)
//
//                    // Crear el objeto ExchangeRateUpdate para la tabla exchange_rate_update
//                    val exchangeRateUpdate = ExchangeRateUpdate(
//                        baseCode = response.base_code,  // Código de la moneda base
//                        lastUpdateUnix = response.time_last_update_unix, // Última actualización en Unix
//                        nextUpdateUnix = response.time_next_update_unix  // Próxima actualización en Unix
//                    )
//
//                    // Insertar la actualización en la tabla exchange_rate_update
//                    exchangeRateUpdateDao.insertUpdate(exchangeRateUpdate)
//
//                    // Mostrar las divisas guardadas
//                    val savedCurrencies = currencyDao.getAllCurrencies()
//                    savedCurrencies.forEach {
//                        Log.d("MainActivity", "Divisa: ${it.code}, Tasa: ${it.rate}")
//                    }
//
//                    Log.d("MainActivity", "Datos de actualización guardados correctamente.")
//                }
//            } catch (e: Exception) {
//                Log.e("MainActivity", "Error al obtener las divisas", e)
//            }
//        }
//    }

//    private fun syncExchangeRates() {
//        lifecycleScope.launch {
//            try {
//                val apiKey = "9af70ebaf64e2aabfb1e8f2b"
//                val response = RetrofitInstance.api.getExchangeRates(apiKey)
//
//                Log.d("MainActivity", "Respuesta completa: $response")
//
//                // Verificar si conversion_rates está vacío o nulo
//                if (response.conversion_rates.isNullOrEmpty()) {
//                    Log.e("MainActivity", "Rates es nulo o vacío.")
//                } else {
//                    // Si hay datos en conversion_rates, procesarlos
//                    val currencies = response.conversion_rates.map { (code, rate) ->
//                        Currency(code = code, rate = rate)
//                    }
//                    currencyDao.insertCurrencies(currencies)
//
//                    // Crear el objeto ExchangeRateUpdate para guardar la información de actualización
//                    val exchangeRateUpdate = ExchangeRateUpdate(
//                        baseCode = response.base_code,  // Código de la moneda base
//                        last_update_unix = response.time_last_update_unix, // Última actualización en Unix
//                        next_update_unix = response.time_next_update_unix  // Próxima actualización en Unix
//                    )
//
//                    // Insertar o actualizar la información en la base de datos
//                    exchangeRateUpdateDao.insertUpdate(exchangeRateUpdate)
//
//                    // Mostrar las divisas guardadas y la última actualización en el Log
//                    val savedCurrencies = currencyDao.getAllCurrencies()
//                    savedCurrencies.forEach {
//                        Log.d("MainActivity", "Divisa: ${it.code}, Tasa: ${it.rate}")
//                    }
//
//                    val lastUpdate = exchangeRateUpdateDao.getLastUpdate()
//                    Log.d("MainActivity", "Última actualización: $lastUpdate")
//                }
//            } catch (e: Exception) {
//                Log.e("MainActivity", "Error al obtener las divisas", e)
//            }
//        }
//    }


    // Función para configurar la sincronización automática utilizando WorkManager
    fun setupAutoSync() {
        // Crea un PeriodicWorkRequest para ejecutar el SyncExchangeRatesWorker cada 1 hora
        val syncRequest = PeriodicWorkRequestBuilder<SyncExchangeRatesWorker>(1, TimeUnit.HOURS)
            // Establece un retraso inicial de 0 segundos, por lo que el trabajo comenzará inmediatamente
            .setInitialDelay(0, TimeUnit.SECONDS)
            //Agregar un tag al worker.
            .addTag("syncExchangeRatesTag")  // Asignamos un tag al Worker
            // Construye la solicitud del trabajo
            .build()

        // Encola la solicitud de trabajo en WorkManager, lo que hace que se ejecute según lo programado
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
                        WorkInfo.State.RUNNING -> Log.d("MainActivity", "El Worker esta en ejecucion.")
                        WorkInfo.State.SUCCEEDED -> Log.d("MainActivity", "El Worker se completo exitosamente.")
                        WorkInfo.State.FAILED -> Log.d("MainActivity", "El Worker fallo.")
                        WorkInfo.State.CANCELLED -> Log.d("MainActivity", "El Worker fue cancelado.")
                        WorkInfo.State.BLOCKED -> Log.d("MainActivity", "El Worker esta bloqueado.")
                        else -> Log.d("MainActivity", "Estado desconocido del Worker: ${workInfo.state}")
                    }
                }
            })
    }
}

//Componentes que estaban por defecto.
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
    DivisasRoomTheme {
        Greeting("Android")
    }
}