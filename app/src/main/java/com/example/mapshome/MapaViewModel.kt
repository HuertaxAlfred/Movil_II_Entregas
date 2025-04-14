package com.example.mapshome

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapshome.Modelos.ApiDirecciones
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class MapaViewModel : ViewModel() {

    val apiKey = BuildConfig.MAPS_API_KEY


    var ubicacionActual by mutableStateOf<LatLng?>(null)
        private set

    var destinoString by mutableStateOf("")
        private set

    var destinoCoordendas by mutableStateOf<LatLng?>(null)
        private set

    // Cambia esto para que sea solo una propiedad de solo lectura
    var puntosDeLaRuta by mutableStateOf<List<LatLng>?>(null)



    private val directionsApi: ApiDirecciones

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        directionsApi = retrofit.create(ApiDirecciones::class.java)
    }

    fun setUbicacionActual(lat: Double, lng: Double) {
        ubicacionActual = LatLng(lat, lng)
    }

    fun actualizarTextoDelTxtDestino(destino: String) {
        destinoString = destino
    }

    fun actualizarDestinoCoordenadas(latLng: LatLng) {
        destinoCoordendas = latLng
    }

    fun obtenerCoordenadasSegunTxtDestino(context: Context, destinoTxtText: String, callback: (LatLng?) -> Unit) {
        val geocoder = Geocoder(context)

        try {
            val addressList = geocoder.getFromLocationName(destinoTxtText, 1)

            if (addressList?.isNotEmpty() == true) {
                // Si la lista no es nula y contiene direcciones, obtenemos la ubicación
                val address = addressList[0]
                val latLng = LatLng(address.latitude, address.longitude)

                // Llamamos al callback con la latitud y longitud
                callback(latLng)
            } else {
                // Si no se encontró ninguna dirección, pasamos null al callback
                callback(null)
            }
        } catch (e: IOException) {
            // Manejar el caso de error de geocodificación
            callback(null)
        }
    }

    fun obtenerRuta() {
        viewModelScope.launch {
            try {
                val origin = "${ubicacionActual?.latitude},${ubicacionActual?.longitude}"
                val destination = "${destinoCoordendas?.latitude},${destinoCoordendas?.longitude}"
                val response = directionsApi.getDirections(
                    origin = origin,
                    destination = destination,
                    apiKey = apiKey
                )
                if (response.isSuccessful) {

                    response.body()?.routes?.firstOrNull()?.descripcionPolilinea?.puntos?.let { encoded ->
                        val decodedPath = decodePolyline(encoded)
                        // Actualiza directamente la propiedad routePoints
                        puntosDeLaRuta = decodedPath
                    }
                } else {
                    Log.e("MAPS", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MAPS", "Excepción al obtener ruta: ${e.message}")
            }
        }
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0

            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dLat = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
            lat += dLat

            shift = 0
            result = 0

            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dLng = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
            lng += dLng

            val latLng = LatLng(lat / 1E5, lng / 1E5)
            poly.add(latLng)
        }

        return poly
    }
}