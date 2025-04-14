package com.example.mapshome.Modelos

import com.google.gson.annotations.SerializedName

data class DescripcionPolilinea(
    @SerializedName("points")
    val puntos: String
)