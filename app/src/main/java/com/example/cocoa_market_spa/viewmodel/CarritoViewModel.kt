
package com.example.cocoa_market_spa.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocoa_market_spa.data.local.AppDatabase
import com.example.cocoa_market_spa.model.CarritoModel
import kotlinx.coroutines.launch

class CarritoViewModel(application: Application) :
    AndroidViewModel(application) {

    // Acceso temporal a los productos de SQLite
    private val productoDao =
        AppDatabase.getDatabase(application).ProductoDao()

    // Productos agregados al carrito
    var productos = mutableStateOf<List<CarritoModel>>(emptyList())
        private set

    // Stock disponible de cada producto
    var stockDisponible = mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    // Mensajes para el comprador
    var mensaje = mutableStateOf("")
        private set

    // Evita operaciones simultaneas
    var operacionEnCurso = mutableStateOf(false)
        private set

    // Agrega productos respetando el stock
    fun agregarProducto(producto: CarritoModel) {

        if (operacionEnCurso.value) return

        if (producto.cantidad <= 0) {
            mensaje.value = "Selecciona una cantidad valida."
            return
        }

        operacionEnCurso.value = true

        viewModelScope.launch {
            try {

                // Consulta el producto actualizado
                val actual = productoDao.obtenerPorId(
                    producto.productoId
                )

                if (actual == null) {

                    mensaje.value = "El producto ya no existe."

                } else {

                    // Guarda el stock disponible
                    stockDisponible.value =
                        stockDisponible.value +
                                (producto.productoId to actual.stock)

                    // Comprueba si ya esta en el carrito
                    val existente = productos.value.find {
                        it.productoId == producto.productoId
                    }

                    val cantidadAnterior =
                        existente?.cantidad ?: 0

                    val cantidadNueva =
                        cantidadAnterior + producto.cantidad

                    // Impide superar el inventario
                    if (cantidadNueva > actual.stock) {

                        mensaje.value =
                            "Stock insuficiente. Disponible: " +
                                    "${actual.stock} unidades."

                        // Avisa tambien en la pantalla de detalle
                        Toast.makeText(
                            getApplication(),
                            mensaje.value,
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        if (existente == null) {

                            // Agrega el producto nuevo
                            productos.value = productos.value +
                                    producto.copy(
                                        nombre = actual.nombre,
                                        precio = actual.precio,
                                        cantidad = cantidadNueva
                                    )

                        } else {

                            // Actualiza un producto existente
                            productos.value = productos.value.map {

                                if (it.productoId == producto.productoId) {

                                    it.copy(
                                        nombre = actual.nombre,
                                        precio = actual.precio,
                                        cantidad = cantidadNueva
                                    )

                                } else {
                                    it
                                }
                            }
                        }

                        // Limpia mensajes anteriores
                        mensaje.value = ""

                        // Vibra cuando se agrega correctamente
                        vibrarConfirmacion()

                        // Muestra mensaje corto de confirmacion
                        Toast.makeText(
                            getApplication(),
                            "Producto agregado al carrito con éxito",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {

                mensaje.value =
                    "No se pudo agregar el producto al carrito."

            } finally {

                operacionEnCurso.value = false
            }
        }
    }

    // Consulta nuevamente el stock y los precios
    fun actualizarStockDisponible() {

        if (operacionEnCurso.value) return

        operacionEnCurso.value = true

        viewModelScope.launch {
            try {

                val nuevosStocks = mutableMapOf<Int, Int>()
                val productosActualizados =
                    mutableListOf<CarritoModel>()

                for (producto in productos.value) {

                    val actual = productoDao.obtenerPorId(
                        producto.productoId
                    )

                    // Guarda el stock actual
                    nuevosStocks[producto.productoId] =
                        actual?.stock ?: 0

                    if (actual != null) {

                        // Actualiza nombre y precio
                        productosActualizados.add(
                            producto.copy(
                                nombre = actual.nombre,
                                precio = actual.precio
                            )
                        )

                    } else {

                        productosActualizados.add(producto)
                    }
                }

                stockDisponible.value = nuevosStocks
                productos.value = productosActualizados

                if (!carritoValido() &&
                    productos.value.isNotEmpty()
                ) {

                    mensaje.value =
                        "Revisa el stock de los productos del carrito."
                }

            } catch (e: Exception) {

                mensaje.value =
                    "No se pudo consultar el stock disponible."

            } finally {

                operacionEnCurso.value = false
            }
        }
    }

    // Aumenta una unidad si existe stock
    fun aumentarCantidad(productoId: Int) {

        if (operacionEnCurso.value) return

        operacionEnCurso.value = true

        viewModelScope.launch {
            try {

                val actual = productoDao.obtenerPorId(
                    productoId
                )

                val stock = actual?.stock ?: 0

                stockDisponible.value =
                    stockDisponible.value +
                            (productoId to stock)

                val producto = productos.value.find {
                    it.productoId == productoId
                }

                if (producto != null) {

                    if (producto.cantidad < stock &&
                        actual != null
                    ) {

                        // Aumenta la cantidad y actualiza precio
                        productos.value = productos.value.map {

                            if (it.productoId == productoId) {

                                it.copy(
                                    cantidad = it.cantidad + 1,
                                    precio = actual.precio
                                )

                            } else {
                                it
                            }
                        }

                        mensaje.value = ""

                        // Vibra solamente si aumento correctamente
                        vibrarConfirmacion()

                    } else {

                        mensaje.value =
                            "No hay mas unidades disponibles."
                    }
                }

            } catch (e: Exception) {

                mensaje.value =
                    "No se pudo aumentar la cantidad."

            } finally {

                operacionEnCurso.value = false
            }
        }
    }

    // Disminuye una unidad sin bajar de 1
    fun disminuirCantidad(productoId: Int) {

        if (operacionEnCurso.value) return

        productos.value = productos.value.map {

            if (it.productoId == productoId &&
                it.cantidad > 1
            ) {

                it.copy(
                    cantidad = it.cantidad - 1
                )

            } else {
                it
            }
        }

        mensaje.value = ""
    }

    // Elimina un producto del carrito
    fun eliminarProducto(productoId: Int) {

        if (operacionEnCurso.value) return

        productos.value = productos.value.filter {
            it.productoId != productoId
        }

        stockDisponible.value =
            stockDisponible.value - productoId

        mensaje.value = ""
    }

    // Vacia todo el carrito
    fun vaciarCarrito() {

        if (operacionEnCurso.value) return

        productos.value = emptyList()
        stockDisponible.value = emptyMap()
        mensaje.value = ""
    }

    // Calcula el total de la compra
    fun calcularTotal(): Int {

        return productos.value.sumOf {
            it.precio * it.cantidad
        }
    }

    // Comprueba que el carrito sea valido
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

    // VIBRACION NATIVA
    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    private fun vibrarConfirmacion() {

        try {

            val context = getApplication<Application>()

            val vibrador: Vibrator

            // Android 12 o superior usa VibratorManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                val manager = context.getSystemService(
                    Context.VIBRATOR_MANAGER_SERVICE
                ) as VibratorManager

                vibrador = manager.defaultVibrator

            } else {

                // Versiones anteriores usan Vibrator
                vibrador = context.getSystemService(
                    Context.VIBRATOR_SERVICE
                ) as Vibrator
            }

            // Comprueba que el telefono tenga vibracion
            if (vibrador.hasVibrator()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    // Vibracion de 150 milisegundos
                    vibrador.vibrate(
                        VibrationEffect.createOneShot(
                            150L,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )

                } else {

                    // Compatibilidad con Android antiguo
                    vibrador.vibrate(150L)
                }
            }

        } catch (e: Exception) {

            // Si falla la vibracion, el carrito sigue funcionando
        }
    }
}
