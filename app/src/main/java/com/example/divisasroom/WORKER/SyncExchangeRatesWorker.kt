package com.example.divisasroom.WORKER
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.util.Log
import com.example.divisasroom.DB.DBPruebas
import com.example.divisasroom.ENTIDADES.Currency
import com.example.divisasroom.ENTIDADES.ExchangeRateUpdate
import com.example.divisasroom.RETROFIT.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

//Worker que utiliza WorkManager para realizar tareas
// en segundo plano relacionadas con la sincronización de tasas de cambio de divisas.
class SyncExchangeRatesWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private val db = DBPruebas.getDatabase(appContext)  // Inicializar la base de datos dentro del Worker
    private val currencyDao = db.currencyDao()  // Inicializar currencyDao
    private val exchangeRateUpdateDao = db.exchangeRateUpdateDao()  // Inicializar exchangeRateUpdateDao
    private val TAG = "SyncExchangeRatesWorker" //Nombre del worker

    //Metodo principal que WorkManager ejecutará cuando se inicie el worker.
    override fun doWork(): Result {
        // Agregar un log al inicio del trabajo
        Log.d(TAG, "SyncExchangeRatesWorker iniciado")
        return try {
            // Ejecutar la lógica de sincronización dentro de una corrutina
            val result = runBlocking { // Iniciamos una corrutina aquí
                //Este metodo es el que realiza una solicitud a una API externa
                syncExchangeRates() // Llamada a la función suspendida
            }
            // Si todo va bien, devolver éxito
            if (result) {
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("SyncExchangeRatesWorker", "Error al sincronizar las divisas", e)
            Result.failure()
        }
    }

    private suspend fun syncExchangeRates(): Boolean {
        return try {
            val apiKey = "9af70ebaf64e2aabfb1e8f2b"
            //Esta solicitud es asíncrona y se espera que devuelva una respuesta, que se guarda en la variable response
            val response = RetrofitInstance.api.getExchangeRates(apiKey)
            Log.d("SyncExchangeRatesWorker", "Respuesta completa: $response")

            // Verificar si conversion_rates está vacío o nulo
            if (response.conversion_rates.isNullOrEmpty()) {
                Log.e("SyncExchangeRatesWorker", "Rates es nulo o vacio.")
                return false
            } else {
                // Si hay datos en conversion_rates, procesarlos
                val currencies = response.conversion_rates.map { (code, rate) ->
                    Currency(code = code, rate = rate)
                }

                // Insertar las divisas en la base de datos
                //Asegura que la operación de inserción en la base de datos se realice en un hilo
                // de trabajo adecuado para tareas de entrada/salida, evitando que el hilo principal
                // (UI thread) se bloquee mientras se realiza la operación.
                withContext(Dispatchers.IO) {
                    currencyDao.insertCurrencies(currencies)
                }

                // Crear el objeto ExchangeRateUpdate para guardar la información de actualización
                val exchangeRateUpdate = ExchangeRateUpdate(
                    baseCode = response.base_code,
                    last_update_unix = response.time_last_update_unix,
                    next_update_unix = response.time_next_update_unix
                )

                // Insertar o actualizar la información en la base de datos
                withContext(Dispatchers.IO) {
                    exchangeRateUpdateDao.insertUpdate(exchangeRateUpdate)
                }

                // Mostrar las divisas guardadas y la última actualización en el Log
                val savedCurrencies = currencyDao.getAllCurrencies()
                savedCurrencies.forEach {
                    Log.d("SyncExchangeRatesWorker", "Divisa: ${it.code}, Tasa: ${it.rate}")
                }
                val lastUpdate = exchangeRateUpdateDao.getLastUpdate()
                Log.d("SyncExchangeRatesWorker", "Ultima actualizacion: $lastUpdate")
                //Todo se completo con exito regresa true
                true
            }
        } catch (e: Exception) {
            Log.e("SyncExchangeRatesWorker", "Error al obtener las divisas", e)
            false
        }
    }
}
