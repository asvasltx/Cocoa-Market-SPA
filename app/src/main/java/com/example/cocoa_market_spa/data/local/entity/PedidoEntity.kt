
package com.example.cocoa_market_spa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val numero: String,
    val total: Int,
    val estado: String = "Confirmado"
)
