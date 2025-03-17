package com.example.divisasroom.RETROFIT

import com.example.divisasroom.ENTIDADES.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Realizar la solicitud GET para obtener las tasas de cambio de las moneda "MXN" usando la clave de la API
 * API premium para cambios en horas.
 */

interface ExchangeRateApi {
    @GET("{apiKey}/latest/MXN")
    suspend fun getExchangeRates(@Path("apiKey") apiKey: String): ExchangeRateResponse
}




