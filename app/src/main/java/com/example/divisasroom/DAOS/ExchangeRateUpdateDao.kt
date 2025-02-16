package com.example.divisasroom.DAOS

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.divisasroom.ENTIDADES.ExchangeRateUpdate

@Dao
interface ExchangeRateUpdateDao {
    // Insertar o actualizar la información de actualización
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdate(update: ExchangeRateUpdate)

    // Obtener la última actualización
    @Query("SELECT * FROM exchange_rate_update ORDER BY id DESC LIMIT 1")
    suspend fun getLastUpdate(): ExchangeRateUpdate?
}