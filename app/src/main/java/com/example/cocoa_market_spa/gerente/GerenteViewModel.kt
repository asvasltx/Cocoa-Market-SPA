
package com.example.cocoa_market_spa.gerente

import android.app.Application
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
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

    // Base de datos utilizada temporalmente.
    private val database =
        AppDatabase.getDatabase(application)

    private val productoDao = database.ProductoDao()
    private val pedidoDao = database.PedidoDao()

    var productos =
        mutableStateOf<List<ProductoEntity>>(emptyList())
        private set

    var pedidos =
        mutableStateOf<List<PedidoEntity>>(emptyList())
        private set

    var detallesPedido =
        mutableStateOf<List<DetallePedidoEntity>>(emptyList())
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

                mensaje.value =
                    "No se pudieron cargar los datos."
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

    // Notifica cuando quedan pocas unidades.
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

    // Guarda un producto nuevo o modifica uno existente.
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

                // Evita productos duplicados.
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

                    productoDao.insertarProducto(
                        ProductoEntity(
                            nombre = nombreLimpio,
                            precio = precio,
                            categoria = categoriaLimpia,
                            descripcion = descripcionLimpia,
                            stock = stock,
                            imagen =
                                R.drawable.ic_launcher_foreground
                        )
                    )

                } else {

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

                mensaje.value =
                    "Producto guardado correctamente."

                confirmarCambioNativo()
                avisarStockBajo(nombreLimpio, stock)

                onGuardado()

            } catch (e: Exception) {

                mensaje.value =
                    "No se pudo guardar el producto."

            } finally {

                operacionEnCurso.value = false
            }
        }
    }

    // Modifica una unidad del inventario.
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

                    mensaje.value = "Stock actualizado."
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

                    mensaje.value =
                        "No se pudo modificar el stock."
                }

            } catch (e: Exception) {

                mensaje.value =
                    "Error al actualizar el inventario."

            } finally {

                operacionEnCurso.value = false
            }
        }
    }

    // Abre o cierra los productos de un pedido.
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

                val detalles =
                    pedidoDao.obtenerDetalles(pedidoId)

                if (idDetalleAbierto.value == pedidoId) {
                    detallesPedido.value = detalles
                }

            } catch (e: Exception) {

                mensaje.value =
                    "No se pudo cargar el detalle."

            } finally {

                if (idDetalleAbierto.value == pedidoId) {
                    cargandoDetalles.value = false
                }
            }
        }
    }

    // Avanza el pedido segun su tipo de entrega.
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

            mensaje.value =
                "Este pedido ya no puede avanzar."
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

                    confirmarCambioNativo()

                    // Conserva el aviso local de pedido listo.
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

                mensaje.value =
                    "No se pudo actualizar el pedido."

            } finally {

                operacionEnCurso.value = false
            }
        }
    }

    fun limpiarMensaje() {
        mensaje.value = ""
    }

    // Vibracion y sonido que ya utilizaba la gerencia.
    @Suppress("DEPRECATION")
    private fun confirmarCambioNativo() {

        try {

            val context = getApplication<Application>()

            val vibrador = context.getSystemService(
                Context.VIBRATOR_SERVICE
            ) as? Vibrator

            if (vibrador != null &&
                vibrador.hasVibrator()
            ) {

                if (Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.O
                ) {

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

            val sonido = RingtoneManager.getRingtone(
                context,
                RingtoneManager.getDefaultUri(
                    RingtoneManager.TYPE_NOTIFICATION
                )
            )

            sonido?.play()

        } catch (e: Exception) {

            // No afecta el cambio ya guardado.
        }
    }
}
