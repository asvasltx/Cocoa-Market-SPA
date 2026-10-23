package com.example.cocoa_market_spa.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrito")
data class CarritoModel(
    @PrimaryKey
    val productoId: Int,
    val nombre: String,
    val precio: Int,
    val cantidad: Int,
    val imagen: Int
)
