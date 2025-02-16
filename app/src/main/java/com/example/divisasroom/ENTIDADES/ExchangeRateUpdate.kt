package com.example.divisasroom.ENTIDADES

import androidx.room.Entity
import androidx.room.PrimaryKey

//Aquí se almacena información sobre la última actualización de las tasas de cambio,
//como el código de la moneda base y las fechas de actualización.
@Entity(tableName = "exchange_rate_update")
data class ExchangeRateUpdate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val baseCode: String, // Código de la moneda base
    val last_update_unix: Long, // Última actualización en Unix
    val next_update_unix: Long // Próxima actualización en Unix
)