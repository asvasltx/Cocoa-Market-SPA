
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

    // Acceso a las tablas de pedidos y productos
    private val pedidoDao = database.PedidoDao()
    private val productoDao = database.ProductoDao()

    // Pedidos que mostrara la pantalla
    var pedidos = mutableStateOf<List<PedidoModel>>(emptyList())
        private set

    // Controla cuando se registra una compra
    var guardandoPedido = mutableStateOf(false)
        private set

    // Mensaje de error al registrar un pedido
    var errorPedido = mutableStateOf("")
        private set

    // Indica cuando se consulta el historial
    var cargandoPedidos = mutableStateOf(false)
        private set

    // Mensaje de error del historial
    var errorCarga = mutableStateOf("")
        private set

    // NUEVO: recupera los pedidos guardados en SQLite
    fun cargarPedidos() {

        viewModelScope.launch {

            cargandoPedidos.value = true
            errorCarga.value = ""

            try {

                // Consulta todos los pedidos registrados
                val pedidosGuardados = pedidoDao
                    .obtenerPedidos()
                    .sortedByDescending { it.id }

                val listaPedidos = mutableListOf<PedidoModel>()

                // Recupera los productos de cada pedido
                for (pedido in pedidosGuardados) {

                    val detalles = pedidoDao.obtenerDetalles(
                        pedido.id
                    )

                    val productosPedido = detalles.map { detalle ->

                        CarritoModel(
                            productoId = detalle.productoId,
                            nombre = detalle.nombre,
                            precio = detalle.precio,
                            cantidad = detalle.cantidad,
                            imagen = detalle.imagen
                        )
                    }

                    // Conserva datos de entrega y pago
                    // si el pedido aun existe en memoria
                    val pedidoAnterior = pedidos.value.find {
                        it.numero == pedido.numero
                    }

                    listaPedidos.add(
                        PedidoModel(
                            numero = pedido.numero,
                            total = pedido.total,
                            productos = productosPedido,
                            estado = pedido.estado,
                            tipoEntrega = pedidoAnterior?.tipoEntrega ?: "",
                            direccion = pedidoAnterior?.direccion ?: "",
                            metodoPago = pedidoAnterior?.metodoPago ?: ""
                        )
                    )
                }

                // Actualiza el historial de la pantalla
                pedidos.value = listaPedidos

            } catch (e: Exception) {

                errorCarga.value =
                    "No se pudo cargar el historial de pedidos."

            } finally {

                cargandoPedidos.value = false
            }
        }
    }

    // Guarda el pedido despues de validar los datos
    fun agregarPedido(
        total: Int,
        productos: List<CarritoModel>,
        tipoEntrega: String,
        metodoPago: String,
        direccion: String,
        onGuardado: () -> Unit
    ) {

        // Impide registrar dos pedidos al mismo tiempo
        if (guardandoPedido.value) return

        if (productos.isEmpty()) {
            errorPedido.value = "El carrito esta vacio."
            return
        }

        // Valida la direccion cuando hay despacho
        if (tipoEntrega == "Domicilio" &&
            direccion.trim().length < 8
        ) {
            errorPedido.value = "Ingresa una direccion valida."
            return
        }

        val productosConfirmados = productos.map {
            it.copy()
        }

        guardandoPedido.value = true
        errorPedido.value = ""

        viewModelScope.launch {

            try {

                // Numero unico para identificar la compra
                val numero = "CM-" +
                        UUID.randomUUID().toString().take(8).uppercase()

                // Guarda todas las operaciones juntas
                database.withTransaction {

                    var totalActual = 0L

                    val detalles =
                        mutableListOf<DetallePedidoEntity>()

                    for (item in productosConfirmados) {

                        // Consulta el precio y stock actual
                        val actual = productoDao.obtenerPorId(
                            item.productoId
                        ) ?: throw IllegalStateException(
                            "Un producto ya no existe."
                        )

                        // Comprueba que exista stock
                        if (item.cantidad <= 0 ||
                            item.cantidad > actual.stock
                        ) {
                            throw IllegalStateException(
                                "Stock insuficiente de ${actual.nombre}."
                            )
                        }

                        // Calcula el precio real del pedido
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

                    // Evita confirmar con precios desactualizados
                    if (totalActual != total.toLong()) {
                        throw IllegalStateException(
                            "Cambio el precio de un producto. " +
                                    "Regresa al catalogo y actualiza el carrito."
                        )
                    }

                    // Registra el pedido
                    val pedidoId = pedidoDao.guardarPedido(
                        PedidoEntity(
                            numero = numero,
                            total = total,
                            estado = "Confirmado"
                        )
                    )

                    // Guarda detalles y descuenta stock
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

                // Agrega el pedido a la lista actual
                pedidos.value = pedidos.value + PedidoModel(
                    numero = numero,
                    total = total,
                    productos = productosConfirmados,
                    estado = "Confirmado",
                    tipoEntrega = tipoEntrega,
                    direccion = direccion,
                    metodoPago = metodoPago
                )

                // Notifica que el pedido se guardo
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
