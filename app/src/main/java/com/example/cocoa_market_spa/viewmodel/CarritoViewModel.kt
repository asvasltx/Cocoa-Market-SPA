package com.example.cocoa_market_spa.viewmodel


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cocoa_market_spa.model.CarritoModel


class CarritoViewModel : ViewModel() {


    var productos = mutableStateOf<List<CarritoModel>>(emptyList())
        private set



    fun agregarProducto(producto: CarritoModel) {

        val existente = productos.value.find {
            it.productoId == producto.productoId
        }

        if (existente != null) {

            productos.value = productos.value.map {
                if (it.productoId == producto.productoId) {
                    it.copy(cantidad = it.cantidad + producto.cantidad)
                } else {
                    it
                }
            }

        } else {
            productos.value = productos.value + producto
        }

    }



        fun aumentarCantidad(productoId: Int) {
            productos.value = productos.value.map { producto ->
                if (producto.productoId == productoId) {
                    producto.copy(cantidad = producto.cantidad + 1)
                } else {
                    producto
                }
            }
        }

        fun disminuirCantidad(productoId: Int) {
            productos.value = productos.value.map { producto ->
                if (producto.productoId == productoId &&
                    producto.cantidad > 1
                ) {
                    producto.copy(cantidad = producto.cantidad - 1)
                } else {
                    producto
                }
            }
        }

        fun eliminarProducto(productoId: Int) {
            productos.value = productos.value.filter {
                it.productoId != productoId
            }
        }

        fun vaciarCarrito() {
            productos.value = emptyList()
        }

        fun calcularTotal(): Int {
            return productos.value.sumOf {
                it.precio * it.cantidad
            }
        }

    }

