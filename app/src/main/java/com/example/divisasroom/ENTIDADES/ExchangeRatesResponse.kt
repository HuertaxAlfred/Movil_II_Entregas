package com.example.divisasroom.ENTIDADES

//Representa la estructura de la respuesta de la API que obtengo
// cuando realizao una solicitud para obtener las tasas de cambio.

data class ExchangeRatesResponse(
    val base_code: String,  // Código de la moneda base
    val conversion_rates: Map<String, Double>,  // Mapa de divisas con sus tasas de cambio
    val time_last_update_unix: Long, // Última actualización en Unix
    val time_next_update_unix: Long // Próxima actualización en Unix
)
