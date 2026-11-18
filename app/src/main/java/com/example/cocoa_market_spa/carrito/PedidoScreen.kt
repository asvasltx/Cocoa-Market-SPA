
package com.example.cocoa_market_spa.carrito

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cocoa_market_spa.model.CarritoModel
import com.example.cocoa_market_spa.viewmodel.PedidoViewModel

@Composable
fun PedidoScreen(
    onVolvercarrito: () -> Unit,
    pedidoViewModel: PedidoViewModel
) {

    // Obtiene los pedidos del ViewModel
    val pedidos = pedidoViewModel.pedidos.value

    // NUEVO: consulta los pedidos al abrir la pantalla
    LaunchedEffect(Unit) {
        pedidoViewModel.cargarPedidos()
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

            Text(
                text = "Mis pedidos",
                style = MaterialTheme.typography.titleLarge
            )

            Button(
                onClick = onVolvercarrito
            ) {
                Text("Volver al carrito")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Muestra un indicador mientras consulta SQLite
            if (pedidoViewModel.cargandoPedidos.value) {

                Text("Cargando pedidos...")

            } else if (
                pedidoViewModel.errorCarga.value.isNotEmpty()
            ) {

                // Muestra el problema y permite reintentar
                Text(
                    text = pedidoViewModel.errorCarga.value,
                    color = MaterialTheme.colorScheme.error
                )

                Button(
                    onClick = {
                        pedidoViewModel.cargarPedidos()
                    }
                ) {
                    Text("Reintentar")
                }

            } else if (pedidos.isEmpty()) {

                // Mensaje cuando no existen pedidos
                Text("Todavia no tienes pedidos registrados.")

            } else {

                // Lista con las compras registradas
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(pedidos) { pedido ->

                        TarjetaPedido(
                            numero = pedido.numero,
                            total = pedido.total,
                            estado = pedido.estado,
                            productos = pedido.productos
                        )
                    }
                }
            }
        }
    }
}

// Tarjeta que muestra la informacion de un pedido
@Composable
fun TarjetaPedido(
    numero: String,
    total: Int,
    estado: String,
    productos: List<CarritoModel>
) {

    // Permite mostrar u ocultar los productos
    var mostrarDetalle by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Identificador del pedido
            Text(
                text = numero,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Total de la compra
            Text("Total: $$total")

            // Estado real que tiene registrado el pedido
            Text("Estado: $estado")

            Spacer(modifier = Modifier.height(8.dp))

            // Abre o cierra los detalles del pedido
            Button(
                onClick = {
                    mostrarDetalle = !mostrarDetalle
                }
            ) {
                Text(
                    if (mostrarDetalle) {
                        "Ocultar detalle"
                    } else {
                        "Ver detalle"
                    }
                )
            }

            if (mostrarDetalle) {

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Productos del pedido",
                    style = MaterialTheme.typography.titleMedium
                )

                // Muestra los productos comprados
                for (producto in productos) {

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = producto.nombre
                    )

                    Text(
                        text = "Cantidad: ${producto.cantidad}"
                    )

                    Text(
                        text = "Subtotal: " +
                                "$${producto.precio * producto.cantidad}"
                    )
                }
            }
        }
    }
}
