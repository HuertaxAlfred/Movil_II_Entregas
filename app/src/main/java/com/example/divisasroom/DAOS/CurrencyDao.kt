package com.example.divisasroom.DAOS

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.divisasroom.ENTIDADES.Currency

@Dao
interface CurrencyDao {

    // Insertar o actualizar divisas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: List<Currency>)

    // Obtener todas las divisas
    @Query("SELECT * FROM currency")
    suspend fun getAllCurrencies(): List<Currency>

    // Obtener una divisa por su código
    @Query("SELECT * FROM currency WHERE code = :currencyCode")
    suspend fun getCurrencyByCode(currencyCode: String): Currency?
}