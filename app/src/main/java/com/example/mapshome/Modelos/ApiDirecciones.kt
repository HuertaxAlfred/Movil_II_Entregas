package com.example.mapshome.Modelos

import com.example.mapshome.RespuestaDirecciones
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//Hacer las consultas a la API
interface ApiDirecciones {
    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String = "driving",
        @Query("key") apiKey: String
    ): Response<RespuestaDirecciones>
}