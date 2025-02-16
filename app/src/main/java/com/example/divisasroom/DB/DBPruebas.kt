package com.example.divisasroom.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.divisasroom.DAOS.CurrencyDao
import com.example.divisasroom.DAOS.ExchangeRateUpdateDao
import com.example.divisasroom.ENTIDADES.Currency
import com.example.divisasroom.ENTIDADES.ExchangeRateUpdate

//Clase utilizada para el desarrollo de la base de datos
@Database(
    entities = [Currency::class, ExchangeRateUpdate::class],
    version = 2,
    exportSchema = false
)
abstract class DBPruebas : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao
    abstract fun exchangeRateUpdateDao(): ExchangeRateUpdateDao

    companion object {
        @Volatile
        private var INSTANCE: DBPruebas? = null

        fun getDatabase(context: Context): DBPruebas {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DBPruebas::class.java,
                    "exchange_rates_db"
                )
                    .fallbackToDestructiveMigration() // Esto me sirve para borrar la base de datos en caso de un cambio de esquema sin migración
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}