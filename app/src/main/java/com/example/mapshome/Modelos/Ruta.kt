package com.example.mapshome.Modelos

import com.google.gson.annotations.SerializedName

data class Ruta(
    @SerializedName("overview_polyline")
    val descripcionPolilinea: DescripcionPolilinea
)


