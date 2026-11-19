
package com.example.cocoa_market_spa.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.cocoa_market_spa.carrito.ConfiguracionEntrega
import com.example.cocoa_market_spa.data.local.AppDatabase
import com.example.cocoa_market_spa.data.local.entity.DetallePedidoEntity
import com.example.cocoa_market_spa.data.local.entity.PedidoEntity
import com.example.cocoa_market_spa.model.CarritoModel
import com.example.cocoa_market_spa.model.PedidoModel
import kotlinx.coroutines.launch
import java.util.UUID

class PedidoViewModel(
    private val database: AppDatabase,
    private val usuarioEmail: String
) : ViewModel() {

    private val pedidoDao = database.PedidoDao()
    private val productoDao = database.ProductoDao()

    var pedidos = mutableStateOf<List<PedidoModel>>(emptyList())
        private set

    var guardandoPedido = mutableStateOf(false)
        private set

    var errorPedido = mutableStateOf("")
        private set

    var cargandoPedidos = mutableStateOf(false)
        private set

    var errorCarga = mutableStateOf("")
        private set

    // Carga solamente los pedidos del comprador autenticado.
    fun cargarPedidos() {

        viewModelScope.launch {

            cargandoPedidos.value = true
            errorCarga.value = ""

            try {

                val guardados = if (usuarioEmail.isBlank()) {
                    emptyList()
                } else {
                    pedidoDao.obtenerPedidosPorUsuario(usuarioEmail)
                }

                val historial = mutableListOf<PedidoModel>()

                for (pedido in guardados) {

                    val detalles =
                        pedidoDao.obtenerDetalles(pedido.id)

                    val productosPedido = detalles.map { detalle ->

                        CarritoModel(
                            productoId = detalle.productoId,
                            nombre = detalle.nombre,
                            precio = detalle.precio,
                            cantidad = detalle.cantidad,
                            imagen = detalle.imagen
                        )
                    }

                    historial.add(
                        PedidoModel(
                            numero = pedido.numero,
                            total = pedido.total,
                            productos = productosPedido,
                            estado = pedido.estado,
                            tipoEntrega = pedido.tipoEntrega,
                            direccion = pedido.direccion,
                            metodoPago = pedido.metodoPago,
                            costoDespacho = pedido.costoDespacho
                        )
                    )
                }

                pedidos.value = historial

            } catch (e: Exception) {

                errorCarga.value =
                    "No se pudo cargar el historial."

            } finally {

                cargandoPedidos.value = false
            }
        }
    }

    // Guarda una compra y sus detalles en una sola transaccion.
    fun agregarPedido(
        total: Int,
        productos: List<CarritoModel>,
        tipoEntrega: String,
        metodoPago: String,
        direccion: String,
        onGuardado: () -> Unit
    ) {

        if (guardandoPedido.value) return

        // No se pueden registrar pedidos sin propietario.
        if (usuarioEmail.isBlank()) {
            errorPedido.value = "Inicia sesion antes de comprar."
            return
        }

        if (productos.isEmpty()) {
            errorPedido.value = "El carrito esta vacio."
            return
        }

        if (tipoEntrega != "Retiro en tienda" &&
            tipoEntrega != "Domicilio"
        ) {
            errorPedido.value = "Selecciona un tipo de entrega."
            return
        }

        if (metodoPago != "Efectivo" &&
            metodoPago != "Tarjeta"
        ) {
            errorPedido.value = "Selecciona un metodo de pago."
            return
        }

        if (tipoEntrega == "Domicilio" &&
            direccion.trim().length < 8
        ) {
            errorPedido.value = "Ingresa una direccion valida."
            return
        }

        // Copia los productos para mantenerlos durante el guardado.
        val productosConfirmados = productos.map { it.copy() }

        // Tarifa pendiente cuando vale null.
        val costoDespacho = if (tipoEntrega == "Domicilio") {
            ConfiguracionEntrega.COSTO_DESPACHO
        } else {
            0
        }

        // Si la tarifa esta pendiente, el total registrado
        // corresponde solo a los productos.
        val totalConDespacho = total + (costoDespacho ?: 0)

        guardandoPedido.value = true
        errorPedido.value = ""

        viewModelScope.launch {

            try {

                val numero = "CM-" +
                        UUID.randomUUID()
                            .toString()
                            .take(8)
                            .uppercase()

                // Todas las operaciones se completan juntas.
                database.withTransaction {

                    var totalActual = 0L

                    val detalles =
                        mutableListOf<DetallePedidoEntity>()

                    for (item in productosConfirmados) {

                        val actual =
                            productoDao.obtenerPorId(item.productoId)
                                ?: throw IllegalStateException(
                                    "Un producto ya no existe."
                                )

                        if (item.cantidad <= 0 ||
                            item.cantidad > actual.stock
                        ) {
                            throw IllegalStateException(
                                "Stock insuficiente de ${actual.nombre}."
                            )
                        }

                        // Consulta el precio vigente en SQLite.
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

                    // No registra compras con precios desactualizados.
                    if (totalActual != total.toLong()) {
                        throw IllegalStateException(
                            "Cambio el precio de un producto. " +
                                    "Actualiza el carrito."
                        )
                    }

                    // Primero guarda el pedido.
                    val pedidoId = pedidoDao.guardarPedido(
                        PedidoEntity(
                            numero = numero,
                            total = totalConDespacho,
                            estado = "Confirmado",
                            usuarioEmail = usuarioEmail,
                            tipoEntrega = tipoEntrega,
                            direccion = if (
                                tipoEntrega == "Domicilio"
                            ) {
                                direccion.trim()
                            } else {
                                ""
                            },
                            metodoPago = metodoPago,
                            costoDespacho = costoDespacho
                        )
                    )

                    // Despues guarda cada detalle y descuenta stock.
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

                // Actualiza el historial despues de guardar.
                cargarPedidos()

                // Solo notifica el exito si la transaccion termino.
                onGuardado()

            } catch (e: Exception) {

                errorPedido.value = e.message
                    ?: "No se pudo guardar el pedido."

            } finally {

                guardandoPedido.value = false
            }
        }
    }
}
