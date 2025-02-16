package com.example.divisasroom.RETROFIT

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Responsable de crear y configurar una instancia de Retrofit para realizar solicitudes HTTP.
object RetrofitInstance {
    //private const val BASE_URL = "https://v6.exchangerate-api.com/v/"
    //private const val BASE_URL = "https://v6.exchangerate-api.com/"
    private const val BASE_URL = "https://v6.exchangerate-api.com/v6/"
    val api: ExchangeRateApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApi::class.java)
    }
}
