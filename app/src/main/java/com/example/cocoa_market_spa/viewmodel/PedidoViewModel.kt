
package com.example.cocoa_market_spa.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cocoa_market_spa.model.CarritoModel
import com.example.cocoa_market_spa.model.PedidoModel

class PedidoViewModel : ViewModel() {

    var pedidos = mutableStateOf<List<PedidoModel>>(emptyList())
        private set

    fun agregarPedido(total: Int, productos: List<CarritoModel>) {

        val numero = "Pedido N°${pedidos.value.size + 1}"

        val pedido = PedidoModel(
            numero = numero,
            total = total,
            productos = productos.toList()
        )

        pedidos.value = pedidos.value + pedido
    }
}
