
package com.example.cocoa_market_spa.model

// Representa un pedido dentro de la aplicacion.
data class PedidoModel(

    val numero: String,
    val total: Int,
    val productos: List<CarritoModel>,
    val estado: String = "Confirmado",

    // Informacion guardada de entrega y pago.
    val tipoEntrega: String = "Retiro en tienda",
    val direccion: String = "",
    val metodoPago: String = "Efectivo",
    val costoDespacho: Int? = 0
)
