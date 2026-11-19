
package com.example.cocoa_market_spa.data.local.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PedidoDao {

    // Guarda el pedido y devuelve su identificador.
    @Insert
    suspend fun guardarPedido(pedido: PedidoEntity): Long

    // Guarda un producto asociado al pedido.
    @Insert
    suspend fun guardarDetalle(detalle: DetallePedidoEntity)

    // El gerente puede consultar todos los pedidos.
    @Query("SELECT * FROM pedidos ORDER BY id DESC")
    suspend fun obtenerPedidos(): List<PedidoEntity>

    // El comprador solo consulta los pedidos de su correo.
    @Query(
        "SELECT * FROM pedidos " +
                "WHERE LOWER(usuarioEmail) = LOWER(:correo) " +
                "ORDER BY id DESC"
    )
    suspend fun obtenerPedidosPorUsuario(
        correo: String
    ): List<PedidoEntity>

    // Recupera los productos de un pedido.
    @Query(
        "SELECT * FROM detalle_pedidos " +
                "WHERE pedidoId = :pedidoId"
    )
    suspend fun obtenerDetalles(
        pedidoId: Int
    ): List<DetallePedidoEntity>

    // Actualiza el estado si aun coincide con el anterior.
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
