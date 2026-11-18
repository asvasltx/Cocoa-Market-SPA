
package com.example.cocoa_market_spa.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.cocoa_market_spa.data.local.AppDatabase
import com.example.cocoa_market_spa.data.local.entity.DetallePedidoEntity
import com.example.cocoa_market_spa.data.local.entity.PedidoEntity
import com.example.cocoa_market_spa.model.CarritoModel
import com.example.cocoa_market_spa.model.PedidoModel
import kotlinx.coroutines.launch
import java.util.UUID

class PedidoViewModel(
    private val database: AppDatabase
) : ViewModel() {

    // Acceso a los pedidos y productos locales
    private val pedidoDao = database.PedidoDao()
    private val productoDao = database.ProductoDao()

    // Lista de pedidos durante la sesion
    var pedidos = mutableStateOf<List<PedidoModel>>(emptyList())
        private set

    // Evita registrar el mismo pedido dos veces
    var guardandoPedido = mutableStateOf(false)
        private set

    // Mensaje que se muestra si ocurre un error
    var errorPedido = mutableStateOf("")
        private set

    // Registra una compra despues de validar sus datos
    fun agregarPedido(
        total: Int,
        productos: List<CarritoModel>,
        tipoEntrega: String,
        metodoPago: String,
        direccion: String,
        onGuardado: () -> Unit
    ) {

        // Evita confirmar mientras otro guardado esta en curso
        if (guardandoPedido.value) return

        if (productos.isEmpty()) {
            errorPedido.value = "El carrito esta vacio."
            return
        }

        if (tipoEntrega == "Domicilio" &&
            direccion.trim().length < 8
        ) {
            errorPedido.value = "Ingresa una direccion valida."
            return
        }

        // Crea una copia de los productos seleccionados
        val productosConfirmados = productos.map {
            it.copy()
        }

        guardandoPedido.value = true
        errorPedido.value = ""

        viewModelScope.launch {

            try {

                // Identificador para el pedido de prueba
                val numero = "CM-" +
                        UUID.randomUUID().toString().take(8).uppercase()

                // Ejecuta todo como una sola transaccion
                // Si falla una operacion, Room revierte los cambios
                database.withTransaction {

                    var totalActual = 0L

                    val detalles =
                        mutableListOf<DetallePedidoEntity>()

                    // Comprueba cada producto del carrito
                    for (item in productosConfirmados) {

                        val actual = productoDao.obtenerPorId(
                            item.productoId
                        ) ?: throw IllegalStateException(
                            "Un producto ya no existe."
                        )

                        // Comprueba que exista stock suficiente
                        if (item.cantidad <= 0 ||
                            item.cantidad > actual.stock
                        ) {
                            throw IllegalStateException(
                                "Stock insuficiente de ${actual.nombre}."
                            )
                        }

                        // Calcula el total usando el precio actual
                        totalActual +=
                            actual.precio.toLong() * item.cantidad

                        detalles.add(
                            DetallePedidoEntity(
                                pedidoId = 0,
                                productoId = actual.id,
                                nombre = actual.nombre,
                                precio = actual.precio,
                                cantidad = item.cantidad,
                                imagen = actual.imagen
                            )
                        )
                    }

                    // No acepta totales con precios antiguos
                    if (totalActual != total.toLong()) {
                        throw IllegalStateException(
                            "Cambio el precio de un producto. " +
                                    "Regresa al catalogo y actualiza el carrito."
                        )
                    }

                    // Guarda primero el pedido
                    val pedidoId = pedidoDao.guardarPedido(
                        PedidoEntity(
                            numero = numero,
                            total = total,
                            estado = "Confirmado"
                        )
                    )

                    // Guarda los detalles y descuenta el stock
                    for (detalle in detalles) {

                        pedidoDao.guardarDetalle(
                            detalle.copy(
                                pedidoId = pedidoId.toInt()
                            )
                        )

                        val actualizado =
                            productoDao.descontarStock(
                                detalle.productoId,
                                detalle.cantidad
                            )

                        if (actualizado != 1) {
                            throw IllegalStateException(
                                "No se pudo descontar el stock."
                            )
                        }
                    }
                }

                // Actualiza los pedidos visibles en la aplicacion
                pedidos.value = pedidos.value + PedidoModel(
                    numero = numero,
                    total = total,
                    productos = productosConfirmados,
                    estado = "Confirmado",
                    tipoEntrega = tipoEntrega,
                    direccion = direccion,
                    metodoPago = metodoPago
                )

                // Avisa a la navegacion que se guardo correctamente
                onGuardado()

            } catch (e: Exception) {

                // Si falla, muestra el error y conserva el carrito
                errorPedido.value = e.message
                    ?: "No se pudo guardar el pedido."

            } finally {

                guardandoPedido.value = false
            }
        }
    }
}
