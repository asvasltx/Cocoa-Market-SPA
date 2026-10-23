package com.example.cocoa_market_spa.data.local.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PedidoDao {

    @Insert
    suspend fun guardarPedido(pedido: PedidoEntity): Long
    @Insert
    suspend fun guardarDetalle(detalle: DetallePedidoEntity)

    @Query("select*from pedidos")
    suspend fun obtenerPedidos(): List<PedidoEntity>

    @Query("select * from detalle_pedidos WHERE pedidoId = :pedidoId")
    suspend fun obtenerDetalles(pedidoId: Int): List<DetallePedidoEntity>
}