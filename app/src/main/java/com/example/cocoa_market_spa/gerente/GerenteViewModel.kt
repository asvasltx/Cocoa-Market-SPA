
package com.example.cocoa_market_spa.gerente

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocoa_market_spa.R
import com.example.cocoa_market_spa.data.local.AppDatabase
import com.example.cocoa_market_spa.data.local.entity.DetallePedidoEntity
import com.example.cocoa_market_spa.data.local.entity.PedidoEntity
import com.example.cocoa_market_spa.data.local.entity.ProductoEntity
import com.example.cocoa_market_spa.notificaciones.Notificaciones
import kotlinx.coroutines.launch

class GerenteViewModel(application: Application) :
    AndroidViewModel(application) {

    // Base de datos temporal para productos y pedidos.
    private val database = AppDatabase.getDatabase(application)
    private val productoDao = database.ProductoDao()
    private val pedidoDao = database.PedidoDao()

    var productos = mutableStateOf<List<ProductoEntity>>(emptyList())
        private set

    var pedidos = mutableStateOf<List<PedidoEntity>>(emptyList())
        private set

    var detallesPedido = mutableStateOf<List<DetallePedidoEntity>>(emptyList())
        private set

    var idDetalleAbierto = mutableStateOf<Int?>(null)
        private set

    var mensaje = mutableStateOf("")
        private set

    var operacionEnCurso = mutableStateOf(false)
        private set

    var cargandoDetalles = mutableStateOf(false)
        private set

    // Recupera productos y pedidos desde Room.
    fun cargarDatos() {
        viewModelScope.launch {
            try {
                productos.value = productoDao.obtenerProductos()

                val lista = pedidoDao.obtenerPedidos()
                pedidos.value = lista

                avisarPedidosNuevos(lista)

            } catch (e: Exception) {
                mensaje.value = "No se pudieron cargar los datos."
            }
        }
    }

    // Notifica pedidos nuevos sin repetir los ya avisados.
    private fun avisarPedidosNuevos(
        listaPedidos: List<PedidoEntity>
    ) {
        val context = getApplication<Application>()

        val preferencias = context.getSharedPreferences(
            "avisos_gerente",
            Context.MODE_PRIVATE
        )

        val ultimoAvisado = preferencias.getInt(
            "ultimo_pedido_avisado",
            0
        )

        val ultimoPedido =
            listaPedidos.maxOfOrNull { it.id } ?: return

        if (ultimoPedido <= ultimoAvisado) return

        val nuevos = listaPedidos.count {
            it.id > ultimoAvisado &&
                    it.estado == "Confirmado"
        }

        if (nuevos > 0) {
            val enviado = Notificaciones.mostrar(
                context = context,
                titulo = "Nuevos pedidos Cocoa Market",
                mensaje = "Tienes $nuevos pedido(s) por revisar."
            )

            if (!enviado) return
        }

        preferencias.edit()
            .putInt("ultimo_pedido_avisado", ultimoPedido)
            .apply()
    }

    // Muestra una alerta cuando el stock es bajo.
    private fun avisarStockBajo(
        nombre: String,
        stock: Int
    ) {
        if (stock <= 5) {
            Notificaciones.mostrar(
                context = getApplication(),
                titulo = "Alerta de inventario",
                mensaje = "$nombre tiene solo $stock unidad(es)."
            )
        }
    }

    // Crea o edita un producto.
    fun guardarProducto(
        productoOriginal: ProductoEntity?,
        nombre: String,
        precioTexto: String,
        categoria: String,
        descripcion: String,
        stockTexto: String,
        onGuardado: () -> Unit
    ) {
        if (operacionEnCurso.value) return

        val nombreLimpio = nombre.trim()
        val categoriaLimpia = categoria.trim()
        val descripcionLimpia = descripcion.trim()

        val precio = precioTexto.toIntOrNull()
        val stock = stockTexto.toIntOrNull()

        if (nombreLimpio.isBlank() ||
            descripcionLimpia.isBlank()
        ) {
            mensaje.value = "Completa nombre y descripcion."
            return
        }

        val categoriasPermitidas = listOf(
            "Abarrotes",
            "Bebidas",
            "Higiene",
            "Lacteos"
        )

        if (categoriaLimpia !in categoriasPermitidas) {
            mensaje.value = "Categoria no valida."
            return
        }

        if (precio == null || precio <= 0) {
            mensaje.value = "El precio debe ser mayor que cero."
            return
        }

        if (stock == null || stock < 0) {
            mensaje.value = "El stock no puede ser negativo."
            return
        }

        operacionEnCurso.value = true
        mensaje.value = ""

        viewModelScope.launch {
            try {
                // Evita productos con el mismo nombre.
                val existe = productoDao.obtenerProductos().any {
                    it.nombre.equals(
                        nombreLimpio,
                        ignoreCase = true
                    ) &&
                            it.id != productoOriginal?.id
                }

                if (existe) {
                    mensaje.value =
                        "Ya existe un producto con ese nombre."
                    return@launch
                }

                if (productoOriginal == null) {
                    // Crea un nuevo producto.
                    productoDao.insertarProducto(
                        ProductoEntity(
                            nombre = nombreLimpio,
                            precio = precio,
                            categoria = categoriaLimpia,
                            descripcion = descripcionLimpia,
                            stock = stock,
                            imagen = R.drawable.ic_launcher_foreground
                        )
                    )

                } else {
                    // Modifica el producto existente.
                    productoDao.actualizarProducto(
                        productoOriginal.copy(
                            nombre = nombreLimpio,
                            precio = precio,
                            categoria = categoriaLimpia,
                            descripcion = descripcionLimpia,
                            stock = stock
                        )
                    )
                }

                productos.value = productoDao.obtenerProductos()

                mensaje.value = "Producto guardado correctamente."

                // Solo vibracion, sin sonido.
                confirmarCambioNativo()

                avisarStockBajo(nombreLimpio, stock)

                onGuardado()

            } catch (e: Exception) {
                mensaje.value = "No se pudo guardar el producto."

            } finally {
                operacionEnCurso.value = false
            }
        }
    }

    // Aumenta o disminuye una unidad del inventario.
    fun modificarStock(
        producto: ProductoEntity,
        cambio: Int
    ) {
        if (operacionEnCurso.value) return
        if (cambio != 1 && cambio != -1) return

        operacionEnCurso.value = true

        viewModelScope.launch {
            try {
                val resultado = productoDao.cambiarStock(
                    producto.id,
                    cambio
                )

                productos.value = productoDao.obtenerProductos()

                if (resultado == 1) {

                    // Muestra el mensaje solo cuando se guardo.
                    mensaje.value = ""

                    Toast.makeText(
                        getApplication<Application>(),
                        "Stock modificado con éxito",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Vibracion breve sin sonido.
                    confirmarCambioNativo()

                    val actualizado = productos.value.find {
                        it.id == producto.id
                    }

                    if (actualizado != null) {
                        avisarStockBajo(
                            actualizado.nombre,
                            actualizado.stock
                        )
                    }

                } else {
                    mensaje.value = "No se pudo modificar el stock."
                }

            } catch (e: Exception) {
                mensaje.value = "Error al actualizar el inventario."

            } finally {
                operacionEnCurso.value = false
            }
        }
    }

    // Abre o cierra los detalles de un pedido.
    fun mostrarDetalles(pedidoId: Int) {

        if (idDetalleAbierto.value == pedidoId) {
            idDetalleAbierto.value = null
            detallesPedido.value = emptyList()
            return
        }

        idDetalleAbierto.value = pedidoId
        detallesPedido.value = emptyList()
        cargandoDetalles.value = true

        viewModelScope.launch {
            try {
                val detalles = pedidoDao.obtenerDetalles(pedidoId)

                if (idDetalleAbierto.value == pedidoId) {
                    detallesPedido.value = detalles
                }

            } catch (e: Exception) {
                mensaje.value = "No se pudo cargar el detalle."

            } finally {
                if (idDetalleAbierto.value == pedidoId) {
                    cargandoDetalles.value = false
                }
            }
        }
    }

    // Cambia el estado del pedido segun la entrega.
    fun avanzarPedido(pedido: PedidoEntity) {

        if (operacionEnCurso.value) return

        val nuevoEstado = when (pedido.estado) {

            "Confirmado" -> "Preparando"

            "Preparando" -> "Listo"

            "Listo" -> {
                if (pedido.tipoEntrega == "Domicilio") {
                    "En despacho"
                } else {
                    "Entregado"
                }
            }

            "En despacho" -> "Entregado"

            else -> null
        }

        if (nuevoEstado == null) {
            mensaje.value = "Este pedido ya no puede avanzar."
            return
        }

        operacionEnCurso.value = true

        viewModelScope.launch {
            try {
                val resultado = pedidoDao.actualizarEstado(
                    pedido.id,
                    pedido.estado,
                    nuevoEstado
                )

                if (resultado == 1) {
                    pedidos.value = pedidoDao.obtenerPedidos()

                    mensaje.value =
                        "Pedido actualizado a $nuevoEstado."

                    // Vibracion sin sonido.
                    confirmarCambioNativo()

                    if (nuevoEstado == "Listo") {
                        Notificaciones.mostrar(
                            context = getApplication(),
                            titulo = "Pedido listo",
                            mensaje = "${pedido.numero} esta listo."
                        )
                    }

                } else {
                    pedidos.value = pedidoDao.obtenerPedidos()

                    mensaje.value =
                        "El pedido cambio. Revisa su estado."
                }

            } catch (e: Exception) {
                mensaje.value = "No se pudo actualizar el pedido."

            } finally {
                operacionEnCurso.value = false
            }
        }
    }

    fun limpiarMensaje() {
        mensaje.value = ""
    }

    // Mantiene vibracion corta
    @Suppress("DEPRECATION")
    private fun confirmarCambioNativo() {

        try {
            val context = getApplication<Application>()

            val vibrador = context.getSystemService(
                Context.VIBRATOR_SERVICE
            ) as? Vibrator

            if (vibrador != null && vibrador.hasVibrator()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrador.vibrate(
                        VibrationEffect.createOneShot(
                            100L,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )

                } else {
                    vibrador.vibrate(100L)
                }
            }

        } catch (e: Exception) {
            // Si falla la vibracion, la operacion sigue funcionando.
        }
    }
}
