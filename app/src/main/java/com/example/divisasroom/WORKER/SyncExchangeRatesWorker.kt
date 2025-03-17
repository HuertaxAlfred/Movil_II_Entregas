package com.example.divisasroom.WORKER

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.divisasroom.DAOS.ExchangeRateDao
import com.example.divisasroom.DB.AppDatabase
import com.example.divisasroom.ENTIDADES.Currency
import com.example.divisasroom.ENTIDADES.ExchangeRateHistory
import com.example.divisasroom.RETROFIT.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SyncExchangeRatesWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private val TAG = "SyncExchangeRatesWorker"
    private val db = AppDatabase.getDatabase(appContext)
    private val dao: ExchangeRateDao = db.exchangeRateDao()

    override fun doWork(): Result {
        Log.d(TAG, "Iniciando sincronizacion de tasas de cambio...")
        return try {
            val result = runBlocking {
                syncExchangeRates()
            }
            if (result)
                Result.success()
            else
                Result.failure()
        } catch (e: Exception) {
            Log.e(TAG, "Error al sincronizar las tasas de cambio", e)
            Result.failure()
        }
    }

    private suspend fun syncExchangeRates(): Boolean {
        return try {
            val apiKey = "4afa1d38b0e3c0cd16571cd8"
            val response = RetrofitInstance.api.getExchangeRates(apiKey)

            if (response.conversion_rates.isEmpty()) {
                Log.e(TAG, "No se recibieron tasas de cambio de la API.")
                return false
            }

            val lastUpdateUnix = response.time_last_update_unix // Timestamp UTC
            val lastUpdateIso = unixToIso8601(lastUpdateUnix)  // Convertir a ISO 8601 (UTC)

            // Verificar si ya hay un registro con este timestamp
            val existingRecords = withContext(Dispatchers.IO) {
                dao.getCountByTimestamp(lastUpdateUnix)
            }

            if (existingRecords > 0) {
                Log.d(
                    TAG,
                    "¡YA existen registros para esta hora ($lastUpdateIso)!, no se insertaran datos duplicados."
                )
                return true // No hacer nada si ya existen registros en esa hora
            }

            // Insertar las divisas en la tabla `currency`
            val currencies = response.conversion_rates.keys.map { code ->
                Currency(code = code, name = code) // Usa el código como nombre temporal
            }
            withContext(Dispatchers.IO) {
                dao.insertCurrencies(currencies)
            }

            /**
             * Insertar tasas de cambio en la tabla `exchange_rate_history`
             * que es la nueva que permite el historial de las divisas
             */
            val exchangeRates = response.conversion_rates.map { (code, rate) ->
                ExchangeRateHistory(
                    currency_code = code,
                    rate = rate,
                    time_last_update_unix = lastUpdateUnix, //Guardar en UTC
                    time_last_update_iso = lastUpdateIso,   //Guardar en formato legible ISO 8601
                    base_code = response.base_code
                )
            }
            withContext(Dispatchers.IO) {
                dao.insertExchangeRates(exchangeRates)
            }

            Log.d(
                TAG,
                "Sincronizacion completada. ${exchangeRates.size} tasas de cambio insertadas en la base de datos."
            )
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener e insertar tasas de cambio", e)
            false
        }
    }

    /**
     * 🔹 Convierte un timestamp UNIX (UTC) a formato ISO 8601 (UTC).
     */
    private fun unixToIso8601(timestamp: Long): String {
        return Instant.ofEpochSecond(timestamp)
            .atZone(ZoneId.of("UTC")) //Mantener en UTC
            .format(DateTimeFormatter.ISO_INSTANT) //"2025-03-11T04:00:00Z"
    }
}
