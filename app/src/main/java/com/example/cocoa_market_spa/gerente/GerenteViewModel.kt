
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

    // Base de datos comercial temporal
    private val database = AppDatabase.getDatabase(application)
    private val productoDao = database.ProductoDao()
    private val pedidoDao = database.PedidoDao()

    // Productos y pedidos del gerente
    var productos = mutableStateOf<List<ProductoEntity>>(emptyList())
        private set

    var pedidos = mutableStateOf<List<PedidoEntity>>(emptyList())
        private set

    // Detalles de un pedido seleccionado
    var detallesPedido = mutableStateOf<List<DetallePedidoEntity>>(emptyList())
        private set

    var idDetalleAbierto = mutableStateOf<Int?>(null)
        private set

    // Control de mensajes y operaciones
    var mensaje = mutableStateOf("")
        private set

    var operacionEnCurso = mutableStateOf(false)
        private set

    var cargandoDetalles = mutableStateOf(false)
        private set

    // Consulta productos y pedidos
    fun cargarDatos() {

        viewModelScope.launch {

            try {

                productos.value = productoDao.obtenerProductos()

                val listaPedidos = pedidoDao.obtenerPedidos()
                pedidos.value = listaPedidos

                // NUEVO: comprueba si llegaron pedidos nuevos
                avisarPedidosNuevos(listaPedidos)

            } catch (e: Exception) {

                mensaje.value = "No se pudieron cargar los datos."
            }
        }
    }

    // NUEVO: notifica una sola vez los pedidos nuevos
    private fun avisarPedidosNuevos(
        listaPedidos: List<PedidoEntity>
    ) {

        val context = getApplication<Application>()

        // Guarda el ultimo pedido avisado en el telefono
        val preferencias = context.getSharedPreferences(
            "avisos_gerente",
            Context.MODE_PRIVATE
        )

        val ultimoAvisado = preferencias.getInt(
            "ultimo_pedido_avisado",
            0
        )

        val ultimoPedido = listaPedidos.maxOfOrNull {
            it.id
        } ?: return

        // No repite notificaciones de pedidos ya revisados
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

            // Guarda el ID solo si pudo publicar el aviso
            if (enviado) {

                preferencias.edit()
                    .putInt(
                        "ultimo_pedido_avisado",
                        ultimoPedido
                    )
                    .apply()
            }

        } else {

            // Si no existen pendientes nuevos, actualiza la marca
            preferencias.edit()
                .putInt(
                    "ultimo_pedido_avisado",
                    ultimoPedido
                )
                .apply()
        }
    }

    // NUEVO: envia una alerta cuando el stock es bajo
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

    // Crea o actualiza un producto
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

        // Valida los datos obligatorios
        if (
            nombreLimpio.isBlank() ||
            descripcionLimpia.isBlank()
        ) {
            mensaje.value = "Completa nombre y descripcion."
            return
        }

        // Mantiene las categorias actuales del catalogo
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

                // Evita productos con nombres duplicados
                val existe = productoDao.obtenerProductos().any {
                    it.nombre.equals(
                        nombreLimpio,
                        ignoreCase = true
                    ) && it.id != productoOriginal?.id
                }

                if (existe) {

                    mensaje.value =
                        "Ya existe un producto con ese nombre."

                    return@launch
                }

                if (productoOriginal == null) {

                    // Crea un producto con imagen temporal
                    val productoNuevo = ProductoEntity(
                        nombre = nombreLimpio,
                        precio = precio,
                        categoria = categoriaLimpia,
                        descripcion = descripcionLimpia,
                        stock = stock,
                        imagen = R.drawable.ic_launcher_foreground
                    )

                    productoDao.insertarProducto(productoNuevo)

                } else {

                    // Modifica el producto conservando su ID
                    val actualizado = productoOriginal.copy(
                        nombre = nombreLimpio,
                        precio = precio,
                        categoria = categoriaLimpia,
                        descripcion = descripcionLimpia,
                        stock = stock
                    )

                    productoDao.actualizarProducto(actualizado)
                }

                // Actualiza el listado visible
                productos.value = productoDao.obtenerProductos()

                mensaje.value = "Producto guardado correctamente."

                // Vibracion, sonido y aviso de stock bajo
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

    // Aumenta o disminuye una unidad de stock
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

                if (resultado == 1) {

                    // Recupera el stock actualizado
                    productos.value = productoDao.obtenerProductos()

                    val actualizado = productos.value.find {
                        it.id == producto.id
                    }

                    mensaje.value = "Stock actualizado."

                    confirmarCambioNativo()

                    // NUEVO: notificacion si quedan pocas unidades
                    if (actualizado != null) {
                        avisarStockBajo(
                            actualizado.nombre,
                            actualizado.stock
                        )
                    }

                } else {

                    productos.value = productoDao.obtenerProductos()

                    mensaje.value =
                        "No se pudo modificar el stock."
                }

            } catch (e: Exception) {

                mensaje.value = "Error al actualizar el inventario."

            } finally {

                operacionEnCurso.value = false
            }
        }
    }

    // Abre o cierra el detalle de un pedido
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

                // Evita mostrar detalles de otro pedido
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

    // Cambia el estado de un pedido
    fun avanzarPedido(pedido: PedidoEntity) {

        if (operacionEnCurso.value) return

        // Orden permitido para preparar y entregar
        val nuevoEstado = when (pedido.estado) {

            "Confirmado" -> "Preparando"
            "Preparando" -> "Listo"
            "Listo" -> "Entregado"
            else -> null
        }

        if (nuevoEstado == null) {
            mensaje.value = "Este pedido ya no puede avanzar."
            return
        }

        operacionEnCurso.value = true

        viewModelScope.launch {

            try {

                // Impide actualizar un estado que ya cambio
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

                    // NUEVO: avisa cuando un pedido queda listo
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

    // Limpia mensajes anteriores
    fun limpiarMensaje() {
        mensaje.value = ""
    }

    // Conserva el sonido y la vibracion ya implementados
    @Suppress("DEPRECATION")
    private fun confirmarCambioNativo() {

        try {

            val context = getApplication<Application>()

            // Vibracion corta al guardar un cambio
            val vibrador = context.getSystemService(
                Context.VIBRATOR_SERVICE
            ) as? Vibrator

            if (
                vibrador != null &&
                vibrador.hasVibrator()
            ) {

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

            // Sonido de confirmacion
            val uri = RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_NOTIFICATION
            )

            val sonido = RingtoneManager.getRingtone(
                context,
                uri
            )

            sonido?.play()

        } catch (e: Exception) {

            // El cambio guardado sigue siendo valido
            // aunque el telefono no tenga estos recursos
        }
    }
}
