package com.example.divisasroom.ENTIDADES

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que sirve para la tabla del historial de las divisas.
 */

@Entity(tableName = "exchange_rate_history")
data class ExchangeRateHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val currency_code: String,       // Código de la moneda (USD, EUR, etc.)
    val rate: Double,                // Tasa de conversión respecto al peso mexicano
    val time_last_update_unix: Long, // Timestamp UNIX (UTC)
    val time_last_update_iso: String, // Fecha en formato ISO 8601 (ej: "2025-03-11T04:00:00Z")
    val base_code: String            // Moneda base (MXN)
)
