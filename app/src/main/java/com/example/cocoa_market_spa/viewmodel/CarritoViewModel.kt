
package com.example.cocoa_market_spa.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocoa_market_spa.data.local.AppDatabase
import com.example.cocoa_market_spa.model.CarritoModel
import kotlinx.coroutines.launch

class CarritoViewModel(application: Application) :
    AndroidViewModel(application) {

    // Acceso a los productos guardados actualmente en SQLite
    private val productoDao =
        AppDatabase.getDatabase(application).ProductoDao()

    // Productos agregados al carrito
    var productos = mutableStateOf<List<CarritoModel>>(emptyList())
        private set

    // Guarda el stock disponible de cada producto
    var stockDisponible = mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    // Muestra mensajes cuando ocurre un problema
    var mensaje = mutableStateOf("")
        private set

    // Evita realizar cambios simultaneos
    var operacionEnCurso = mutableStateOf(false)
        private set

    // Agrega un producto respetando su stock
    fun agregarProducto(producto: CarritoModel) {

        if (operacionEnCurso.value) return

        operacionEnCurso.value = true

        viewModelScope.launch {
            try {

                // Consulta el producto registrado
                val productoActual =
                    productoDao.obtenerPorId(producto.productoId)

                if (productoActual == null) {
                    mensaje.value = "Producto no encontrado"

                } else {

                    val stock = productoActual.stock

                    // Guarda el stock disponible
                    stockDisponible.value =
                        stockDisponible.value +
                                (producto.productoId to stock)

                    val existente = productos.value.find {
                        it.productoId == producto.productoId
                    }

                    val cantidadAnterior =
                        existente?.cantidad ?: 0

                    val nuevaCantidad =
                        cantidadAnterior + producto.cantidad

                    // Nunca supera el stock registrado
                    val cantidadFinal =
                        minOf(nuevaCantidad, stock)

                    if (cantidadFinal <= cantidadAnterior) {

                        mensaje.value =
                            "No hay mas unidades disponibles"

                    } else {

                        if (existente != null) {

                            // Actualiza el producto existente
                            productos.value = productos.value.map {

                                if (it.productoId == producto.productoId) {
                                    it.copy(
                                        cantidad = cantidadFinal,
                                        precio = productoActual.precio
                                    )
                                } else {
                                    it
                                }
                            }

                        } else {

                            // Agrega el producto nuevo
                            productos.value = productos.value +
                                    producto.copy(
                                        cantidad = cantidadFinal,
                                        precio = productoActual.precio
                                    )
                        }

                        mensaje.value = ""
                    }
                }

            } catch (e: Exception) {
                mensaje.value = "Error al agregar el producto"

            } finally {
                operacionEnCurso.value = false
            }
        }
    }

    // Consulta nuevamente el stock de los productos del carrito
    fun actualizarStockDisponible() {

        if (operacionEnCurso.value) return

        operacionEnCurso.value = true

        viewModelScope.launch {
            try {

                val nuevosStocks = mutableMapOf<Int, Int>()

                for (producto in productos.value) {

                    val productoActual =
                        productoDao.obtenerPorId(producto.productoId)

                    nuevosStocks[producto.productoId] =
                        productoActual?.stock ?: 0
                }

                stockDisponible.value = nuevosStocks

            } catch (e: Exception) {
                mensaje.value = "No se pudo consultar el stock"

            } finally {
                operacionEnCurso.value = false
            }
        }
    }

    // Aumenta una unidad si existe stock disponible
    fun aumentarCantidad(productoId: Int) {

        if (operacionEnCurso.value) return

        operacionEnCurso.value = true

        viewModelScope.launch {
            try {

                val productoActual =
                    productoDao.obtenerPorId(productoId)

                val stock = productoActual?.stock ?: 0

                stockDisponible.value =
                    stockDisponible.value + (productoId to stock)

                val productoCarrito = productos.value.find {
                    it.productoId == productoId
                }

                if (productoCarrito != null) {

                    if (productoCarrito.cantidad < stock) {

                        productos.value = productos.value.map {

                            if (it.productoId == productoId) {
                                it.copy(
                                    cantidad = it.cantidad + 1,
                                    precio = productoActual!!.precio
                                )
                            } else {
                                it
                            }
                        }

                        mensaje.value = ""

                    } else {
                        mensaje.value =
                            "No hay mas unidades disponibles"
                    }
                }

            } catch (e: Exception) {
                mensaje.value = "Error al aumentar la cantidad"

            } finally {
                operacionEnCurso.value = false
            }
        }
    }

    // Disminuye una unidad, sin bajar de 1
    fun disminuirCantidad(productoId: Int) {

        if (operacionEnCurso.value) return

        productos.value = productos.value.map {

            if (it.productoId == productoId &&
                it.cantidad > 1
            ) {
                it.copy(cantidad = it.cantidad - 1)
            } else {
                it
            }
        }

        mensaje.value = ""
    }

    // Elimina solamente el producto seleccionado
    fun eliminarProducto(productoId: Int) {

        if (operacionEnCurso.value) return

        productos.value = productos.value.filter {
            it.productoId != productoId
        }

        stockDisponible.value =
            stockDisponible.value - productoId

        mensaje.value = ""
    }

    // Elimina todos los productos del carrito
    fun vaciarCarrito() {

        if (operacionEnCurso.value) return

        productos.value = emptyList()
        stockDisponible.value = emptyMap()
        mensaje.value = ""
    }

    // Calcula el valor total del carrito
    fun calcularTotal(): Int {

        return productos.value.sumOf {
            it.precio * it.cantidad
        }
    }

    // Comprueba que las cantidades respeten el stock
    fun carritoValido(): Boolean {

        if (productos.value.isEmpty()) {
            return false
        }

        for (producto in productos.value) {

            val stock =
                stockDisponible.value[producto.productoId] ?: 0

            if (producto.cantidad <= 0 ||
                producto.cantidad > stock
            ) {
                return false
            }
        }

        return true
    }

    // Limpia los mensajes anteriores
    fun limpiarMensaje() {
        mensaje.value = ""
    }
}
