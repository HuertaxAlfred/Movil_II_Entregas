package com.example.divisasroom.ENTIDADES

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.example.divisasroom.DAOS.ExchangeRateDao
import com.example.divisasroom.DB.AppDatabase

class ContentProvider : ContentProvider() {

    private lateinit var exchangeRateUpdateDao: ExchangeRateDao

    // Método onCreate se llama cuando el provider es creado
    override fun onCreate(): Boolean {
        // Inicializa el acceso a la base de datos
        exchangeRateUpdateDao = AppDatabase.getDatabase(context!!).exchangeRateDao()
        Log.d("ContentProvider", "ContentProvider Creado")
        // Retorna true para indicar que el ContentProvider se ha inicializado correctamente
        return true
    }


    /**
     * Metodo de seleccion de datos encargado de consultar en la base de datos
     */
    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        // Verificar si estamos utilizando el URI correcto
        val cursor = when (uri) {
            Uri.parse("content://com.example.divisasroom.provider/exchange_rates") -> {
                if (selectionArgs != null && selectionArgs.size == 3) {
                    // Llamar al DAO para obtener los registros filtrados por la moneda y rango de fechas solicitados desde el consumidor
                    exchangeRateUpdateDao.getRatesCursor(
                        selectionArgs[0], selectionArgs[1], selectionArgs[2]
                    )
                } else {
                    // Devolver todos los registros si no se proporcionan parámetros de selección
                    exchangeRateUpdateDao.getAllRatesCursor()
                }
            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
        return cursor
    }


    override fun getType(uri: Uri): String? {
        return when (uri) {
            Uri.parse("content://com.example.divisasroom.provider/exchange_rates") -> "vnd.android.cursor.dir/vnd.com.example.divisasroom.provider.exchange_rates"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }
}