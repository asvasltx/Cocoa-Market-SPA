
package com.example.cocoa_market_spa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detalle_pedidos")
data class DetallePedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pedidoId: Int,
    val productoId: Int,
    val nombre: String,
    val precio: Int,
    val cantidad: Int,
    val imagen: Int
)
