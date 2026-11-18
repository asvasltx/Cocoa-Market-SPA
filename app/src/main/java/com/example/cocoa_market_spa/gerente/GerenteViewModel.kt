
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
import kotlinx.coroutines.launch

class GerenteViewModel(application: Application) :
    AndroidViewModel(application) {

    // Acceso temporal a los datos comerciales en SQLite
    private val database = AppDatabase.getDatabase(application)
    private val productoDao = database.ProductoDao()
    private val pedidoDao = database.PedidoDao()

    // Productos visibles en el panel
    var productos = mutableStateOf<List<ProductoEntity>>(emptyList())
        private set

    // Pedidos visibles en el panel
    var pedidos = mutableStateOf<List<PedidoEntity>>(emptyList())
        private set

    // Detalles del pedido seleccionado
    var detallesPedido = mutableStateOf<List<DetallePedidoEntity>>(emptyList())
        private set

    var idDetalleAbierto = mutableStateOf<Int?>(null)
        private set

    // Mensajes y control de operaciones
    var mensaje = mutableStateOf("")
        private set

    var operacionEnCurso = mutableStateOf(false)
        private set

    var cargandoDetalles = mutableStateOf(false)
        private set

    // Consulta los datos guardados
    fun cargarDatos() {

        viewModelScope.launch {
            try {
                productos.value = productoDao.obtenerProductos()
                pedidos.value = pedidoDao.obtenerPedidos()

            } catch (e: Exception) {
                mensaje.value = "No se pudieron cargar los datos."
            }
        }
    }

    // Guarda un producto nuevo o actualiza uno existente
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

        // Valida los campos obligatorios
        if (nombreLimpio.isBlank() ||
            descripcionLimpia.isBlank()
        ) {
            mensaje.value = "Completa el nombre y la descripcion."
            return
        }

        // Valida las categorias del catalogo
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
            mensaje.value = "Ingresa un precio mayor que cero."
            return
        }

        if (stock == null || stock < 0) {
            mensaje.value = "El stock debe ser cero o mayor."
            return
        }

        operacionEnCurso.value = true
        mensaje.value = ""

        viewModelScope.launch {
            try {

                // Evita registrar nombres duplicados
                val existe = productoDao.obtenerProductos().any {
                    it.nombre.equals(
                        nombreLimpio,
                        ignoreCase = true
                    ) && it.id != productoOriginal?.id
                }

                if (existe) {
                    mensaje.value = "Ya existe un producto con ese nombre."
                    return@launch
                }

                if (productoOriginal == null) {

                    // Crea un producto nuevo
                    // Usamos una imagen temporal del proyecto
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

                    // Conserva el ID y la imagen del producto
                    val productoActualizado = productoOriginal.copy(
                        nombre = nombreLimpio,
                        precio = precio,
                        categoria = categoriaLimpia,
                        descripcion = descripcionLimpia,
                        stock = stock
                    )

                    productoDao.actualizarProducto(
                        productoActualizado
                    )
                }

                // Refresca el listado despues de guardar
                productos.value = productoDao.obtenerProductos()

                mensaje.value = "Producto guardado correctamente."

                confirmarCambioNativo()
                onGuardado()

            } catch (e: Exception) {
                mensaje.value = "No se pudo guardar el producto."

            } finally {
                operacionEnCurso.value = false
            }
        }
    }

    // Aumenta o disminuye una unidad del inventario
    fun modificarStock(producto: ProductoEntity, cambio: Int) {

        if (operacionEnCurso.value) return

        // Solo permitimos cambios de una unidad
        if (cambio != 1 && cambio != -1) return

        operacionEnCurso.value = true

        viewModelScope.launch {
            try {

                // Actualiza el stock sin dejar valores negativos
                val resultado = productoDao.cambiarStock(
                    producto.id,
                    cambio
                )

                if (resultado == 1) {

                    productos.value = productoDao.obtenerProductos()
                    mensaje.value = "Stock actualizado."

                    confirmarCambioNativo()

                } else {
                    mensaje.value = "No se pudo modificar el stock."
                    productos.value = productoDao.obtenerProductos()
                }

            } catch (e: Exception) {
                mensaje.value = "Error al actualizar el inventario."

            } finally {
                operacionEnCurso.value = false
            }
        }
    }

    // Consulta los productos incluidos en un pedido
    fun mostrarDetalles(pedidoId: Int) {

        // Si el detalle ya esta abierto, lo cerramos
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
                detallesPedido.value = pedidoDao.obtenerDetalles(
                    pedidoId
                )

            } catch (e: Exception) {
                mensaje.value = "No se pudo cargar el detalle."

            } finally {
                cargandoDetalles.value = false
            }
        }
    }

    // Avanza el estado del pedido
    fun avanzarPedido(pedido: PedidoEntity) {

        if (operacionEnCurso.value) return

        // Orden permitido para preparar una compra
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

                // Confirma que el estado no haya cambiado antes
                val resultado = pedidoDao.actualizarEstado(
                    pedido.id,
                    pedido.estado,
                    nuevoEstado
                )

                if (resultado == 1) {

                    pedidos.value = pedidoDao.obtenerPedidos()
                    mensaje.value = "Pedido actualizado a $nuevoEstado."

                    confirmarCambioNativo()

                } else {

                    pedidos.value = pedidoDao.obtenerPedidos()
                    mensaje.value = "El pedido cambio. Revisa su estado."
                }

            } catch (e: Exception) {
                mensaje.value = "No se pudo actualizar el pedido."

            } finally {
                operacionEnCurso.value = false
            }
        }
    }

    // Borra un mensaje anterior
    fun limpiarMensaje() {
        mensaje.value = ""
    }

    // Vibra y reproduce un sonido cuando se guarda algo
    // El permiso VIBRATE ya existe en AndroidManifest.xml
    @Suppress("DEPRECATION")
    private fun confirmarCambioNativo() {

        try {

            val context = getApplication<Application>()

            // Recurso nativo 1: vibracion
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

            // Recurso nativo 2: sonido de confirmacion
            val uri = RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_NOTIFICATION
            )

            val sonido = RingtoneManager.getRingtone(
                context,
                uri
            )

            sonido?.play()

        } catch (e: Exception) {
            // Si falla un recurso nativo, el cambio guardado
            // en la base de datos no se pierde
        }
    }
}
