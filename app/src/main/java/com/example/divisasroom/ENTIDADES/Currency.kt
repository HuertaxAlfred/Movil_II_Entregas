package com.example.divisasroom.ENTIDADES

import androidx.room.Entity
import androidx.room.PrimaryKey

//Entidad que sirve para almacenar las divisas y sus respectivas tasas de cambio
@Entity(tableName = "currency")
data class Currency(
    @PrimaryKey(autoGenerate = false) val code: String, // Código de la moneda (Ej: USD, EUR)
    val rate: Double // Tasa de conversión respecto a la moneda base
)
