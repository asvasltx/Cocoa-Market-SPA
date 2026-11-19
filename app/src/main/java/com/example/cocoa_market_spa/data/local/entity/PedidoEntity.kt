
package com.example.cocoa_market_spa.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos")
data class PedidoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val numero: String,
    val total: Int,
    val estado: String = "Confirmado",

    // Correo del comprador que realizo el pedido.
    @ColumnInfo(defaultValue = "''")
    val usuarioEmail: String = "",

    // Tipo de entrega elegido.
    @ColumnInfo(defaultValue = "'Retiro en tienda'")
    val tipoEntrega: String = "Retiro en tienda",

    // Direccion utilizada cuando existe despacho.
    @ColumnInfo(defaultValue = "''")
    val direccion: String = "",

    // Metodo seleccionado, no representa un pago realizado.
    @ColumnInfo(defaultValue = "'Efectivo'")
    val metodoPago: String = "Efectivo",

    // null significa que el precio del despacho esta pendiente.
    val costoDespacho: Int? = 0
)
