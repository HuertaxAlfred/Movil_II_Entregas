package com.example.divisasroom.ENTIDADES

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *  Formar como el MODELO de la respuesta de la API
 */
@Entity(tableName = "exchange_rate")
data class ExchangeRateResponse(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val result: String,
    val time_last_update_unix: Long,
    val time_next_update_unix: Long,
    val base_code: String,
    @ColumnInfo(name = "currency_code")
    val currency_code: String,
    @ColumnInfo(name = "conversion_rates")
    val conversion_rates: Map<String, Double> // Este campo será convertido con el TypeConverter
)
