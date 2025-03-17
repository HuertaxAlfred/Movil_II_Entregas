package com.example.divisasroom.DAOS

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.divisasroom.ENTIDADES.Currency
import com.example.divisasroom.ENTIDADES.ExchangeRateHistory

@Dao
interface ExchangeRateDao {

    /**
     * Revisar si hay divisas ya en esa hora para no volver a guardar.
     */
    @Query("SELECT COUNT(*) FROM exchange_rate_history WHERE time_last_update_unix = :timestamp")
    suspend fun getCountByTimestamp(timestamp: Long): Int

    /**
     * Inserción de divisas 162 de la api.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: List<Currency>)

    /**
     * Inserción de múltiples tasas de cambio en el historial.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRates(rates: List<ExchangeRateHistory>)

    /**
     * Obtener todos los registros de la tabla historial de divisas
     */
    @Query("SELECT * FROM exchange_rate_history")
    fun getAllRates(): List<ExchangeRateHistory>

    /**
     * Provar consulta en la BD
     */
    @Query(
        """
        SELECT * FROM exchange_rate_history
        WHERE currency_code = :currencyCode 
        AND time_last_update_iso BETWEEN :startDateIso AND :endDateIso
    """
    )
    fun getRatesByCurrencyAndDateIso(
        currencyCode: String, startDateIso: String, endDateIso: String
    ): List<ExchangeRateHistory>

    // Consulta que devuelve todos los registros en modo cursor
    @Query("SELECT * FROM exchange_rate_history")
    fun getAllRatesCursor(): Cursor

    /**
     * Consulta para devolver registros filtrados por el codigo de moneda y rango de fechas con hora en formato ISO return en cursor
     */
    @Query(
        """
        SELECT * FROM exchange_rate_history
        WHERE currency_code = :currencyCode
        AND time_last_update_iso BETWEEN :startDateIso AND :endDateIso
    """
    )
    fun getRatesCursor(
        currencyCode: String, startDateIso: String, endDateIso: String
    ): Cursor

}
