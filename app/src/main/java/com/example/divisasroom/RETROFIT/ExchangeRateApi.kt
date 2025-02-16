package com.example.divisasroom.RETROFIT

import com.example.divisasroom.ENTIDADES.ExchangeRatesResponse
import retrofit2.http.GET
import retrofit2.http.Path

//interface ExchangeRateApi {
////    @GET("latest/USD")
////    suspend fun getExchangeRates(@Query("apikey") apiKey: String): ExchangeRatesResponse
////}
//
//    @GET("v6/{apiKey}/latest/USD")
//    suspend fun getExchangeRates(@Path("apiKey") apiKey: String): ExchangeRatesResponse
//}


//Realiza una solicitud GET para obtener las tasas de cambio de la moneda "MXN" usando una clave API.
//Clave API la que me da la pagina de cambios.
interface ExchangeRateApi {
    @GET("{apiKey}/latest/MXN")
    suspend fun getExchangeRates(@Path("apiKey") apiKey: String): ExchangeRatesResponse
}
