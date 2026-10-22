package com.example.cocoa_market_spa.model

class PedidoModel(
    val numero: String,
    val total: Int,
    val productos: List<CarritoModel>,
    val estado: String = "Confirmado"
) {
}