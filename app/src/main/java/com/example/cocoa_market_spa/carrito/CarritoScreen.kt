
package com.example.cocoa_market_spa.carrito

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cocoa_market_spa.viewmodel.CarritoViewModel

@Composable
fun CarritoScreen(
    onNavigateToPedidos: () -> Unit,
    onVolverCatalogo: () -> Unit,
    carritoViewModel: CarritoViewModel = viewModel()
) {

    // Obtiene los productos actuales del carrito
    val productosViewModel = carritoViewModel.productos.value

    // Calcula el total de la compra
    val total = carritoViewModel.calcularTotal()

    // Permite mostrar mensajes de Android
    val context = LocalContext.current

    // Consulta el stock al abrir la pantalla
    LaunchedEffect(Unit) {
        carritoViewModel.actualizarStockDisponible()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            // Regresa al catalogo
            Button(
                onClick = onVolverCatalogo,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            ) {
                Text("← Volver al catálogo")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Mi carrito",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Productos agregados al carrito",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Muestra mensajes de validacion
            if (carritoViewModel.mensaje.value.isNotEmpty()) {

                Text(
                    text = carritoViewModel.mensaje.value,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Si no hay productos, muestra un mensaje
            if (productosViewModel.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tu carrito está vacío",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            } else {

                // Lista desplazable de productos
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(productosViewModel) { producto ->

                        // Stock consultado desde SQLite
                        val stock =
                            carritoViewModel.stockDisponible.value[
                                producto.productoId
                            ]

                        ProductoCarrito(
                            producto = producto.nombre,
                            precio = producto.precio,
                            cantidad = producto.cantidad,
                            stock = stock,
                            enProceso =
                                carritoViewModel.operacionEnCurso.value,

                            onAumentar = {
                                carritoViewModel.aumentarCantidad(
                                    producto.productoId
                                )
                            },

                            onDisminuir = {
                                carritoViewModel.disminuirCantidad(
                                    producto.productoId
                                )
                            },

                            onEliminar = {
                                carritoViewModel.eliminarProducto(
                                    producto.productoId
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total actualizado automaticamente
            Text(
                text = "Total del carrito: $$total",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Avanza al pago solamente si el carrito es valido
            Button(
                onClick = {

                    Toast.makeText(
                        context,
                        "Continuar al pago",
                        Toast.LENGTH_SHORT
                    ).show()

                    onNavigateToPedidos()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled =
                    carritoViewModel.carritoValido() &&
                            !carritoViewModel.operacionEnCurso.value
            ) {
                Text("Continuar al pago")
            }
        }
    }
}

// Tarjeta de cada producto agregado al carrito
@Composable
fun ProductoCarrito(
    producto: String,
    precio: Int,
    cantidad: Int,
    stock: Int?,
    enProceso: Boolean,
    onAumentar: () -> Unit,
    onDisminuir: () -> Unit,
    onEliminar: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Nombre del producto
            Text(
                text = producto,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Precio de una unidad
            Text(
                text = "Precio: $$precio",
                style = MaterialTheme.typography.bodyMedium
            )

            // Subtotal del producto
            Text(
                text = "Subtotal: $${precio * cantidad}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Stock disponible
            Text(
                text = "Stock disponible: " +
                        (stock?.toString() ?: "Consultando...")
            )

            // Aviso si el stock ya no alcanza
            if (stock != null && cantidad > stock) {

                Text(
                    text = "Stock insuficiente. Ajusta la cantidad.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // Disminuir cantidad
                Button(
                    onClick = onDisminuir,
                    enabled = cantidad > 1 && !enProceso
                ) {
                    Text("-")
                }

                // Cantidad seleccionada
                Text(
                    text = "$cantidad",
                    style = MaterialTheme.typography.titleMedium
                )

                // Aumentar sin superar el stock
                Button(
                    onClick = onAumentar,
                    enabled =
                        stock != null &&
                                cantidad < stock &&
                                !enProceso
                ) {
                    Text("+")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Eliminar este producto del carrito
            Button(
                onClick = onEliminar,
                enabled = !enProceso
            ) {
                Text("Eliminar producto")
            }
        }
    }
}
