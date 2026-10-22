
package com.example.cocoa_market_spa.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cocoa_market_spa.model.CarritoModel
import com.example.cocoa_market_spa.model.PedidoModel
import com.example.cocoa_market_spa.data.local.entity.PedidoDao
import com.example.cocoa_market_spa.data.local.entity.PedidoEntity
import com.example.cocoa_market_spa.data.local.entity.DetallePedidoEntity
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PedidoViewModel(
    private val pedidoDao: PedidoDao
) : ViewModel() {

    var pedidos = mutableStateOf<List<PedidoModel>>(emptyList())
        private set

    fun agregarPedido(total: Int, productos: List<CarritoModel>) {

        val numero = "Pedido N°${pedidos.value.size + 1}"

        val pedido = PedidoModel(
            numero = numero,
            total = total,
            productos = productos.toList()
        )



        viewModelScope.launch {
            val pedidoEntity = PedidoEntity(
                numero = numero,
                total = total,
                estado = "Confirmado"
            )

            val pedidoId = pedidoDao.guardarPedido(pedidoEntity)

            productos.forEach { producto ->
                val detalle = DetallePedidoEntity(
                    pedidoId = pedidoId.toInt(),
                    productoId = producto.productoId,
                    nombre = producto.nombre,
                    precio = producto.precio,
                    cantidad = producto.cantidad,
                    imagen = producto.imagen
                )

                pedidoDao.guardarDetalle(detalle)
            }
            pedidos.value = pedidos.value + pedido

        }
    }

}
