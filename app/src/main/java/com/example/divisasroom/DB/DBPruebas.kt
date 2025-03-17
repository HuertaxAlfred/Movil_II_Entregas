package com.example.divisasroom.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.divisasroom.DAOS.ExchangeRateDao
import com.example.divisasroom.ENTIDADES.Currency
import com.example.divisasroom.ENTIDADES.ExchangeRateHistory

// Clase utilizada para el desarrollo de la base de datos

//@Database(entities = [Currency::class, ExchangeRateEntity::class], version = 3)
@Database(
    entities = [Currency::class, ExchangeRateHistory::class],
    version = 6, // 🔹 Asegúrate de cambiar este número cuando modifiques la BD
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exchangeRateDao(): ExchangeRateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "exchange_rates_db"
                )
                    .fallbackToDestructiveMigration() // 🔹 Borra y recrea la BD si cambia la versión
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}