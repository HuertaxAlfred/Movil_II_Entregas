package com.example.divisasroom.ENTIDADES

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla de divisas tabla general
 */
@Entity(tableName = "currency")
data class Currency(
    @PrimaryKey(autoGenerate = false) val code: String, // Código de la moneda (Ej: USD, EUR)
    val name: String // Nombre de la divisa (Ej: Dólar, Euro)
)
