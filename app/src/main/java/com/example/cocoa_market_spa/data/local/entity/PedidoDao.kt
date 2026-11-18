
package com.example.cocoa_market_spa.data.local.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PedidoDao {

    // Guarda un pedido y devuelve su ID
    @Insert
    suspend fun guardarPedido(pedido: PedidoEntity): Long

    // Guarda un producto que pertenece al pedido
    @Insert
    suspend fun guardarDetalle(detalle: DetallePedidoEntity)

    // Consulta los pedidos, mostrando primero el mas reciente
    @Query("SELECT * FROM pedidos ORDER BY id DESC")
    suspend fun obtenerPedidos(): List<PedidoEntity>

    // Consulta los productos de un pedido
    @Query(
        "SELECT * FROM detalle_pedidos " +
                "WHERE pedidoId = :pedidoId"
    )
    suspend fun obtenerDetalles(
        pedidoId: Int
    ): List<DetallePedidoEntity>

    // Cambia el estado solo si coincide con el anterior
    // Devuelve 1 si se actualizo y 0 si no se pudo
    @Query(
        "UPDATE pedidos SET estado = :nuevoEstado " +
                "WHERE id = :pedidoId AND estado = :estadoAnterior"
    )
    suspend fun actualizarEstado(
        pedidoId: Int,
        estadoAnterior: String,
        nuevoEstado: String
    ): Int
}
