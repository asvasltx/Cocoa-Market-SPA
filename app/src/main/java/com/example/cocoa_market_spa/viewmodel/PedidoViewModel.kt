package com.example.cocoa_market_spa.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class PedidoViewModel: ViewModel() {
    var pedidos= mutableStateOf<List<Pair<String, Int>>>(emptyList())
        private set
    fun agregarPedido(total: Int){
        val numero=pedidos.value.size+1
        pedidos.value=pedidos.value +
                ("pedido n° $numero" to total)
    }
}