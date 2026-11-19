
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
import com.example.cocoa_market_spa.model.PedidoModel
import com.example.cocoa_market_spa.viewmodel.PedidoViewModel

@Composable
fun PedidoScreen(
    onVolvercarrito: () -> Unit,
    pedidoViewModel: PedidoViewModel
) {

    val pedidos = pedidoViewModel.pedidos.value

    // Actualiza el historial al abrir esta pantalla.
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

            Spacer(modifier = Modifier.height(12.dp))

            if (pedidoViewModel.cargandoPedidos.value) {

                Text("Cargando pedidos...")

            } else if (
                pedidoViewModel.errorCarga.value.isNotEmpty()
            ) {

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

                Text("Todavia no tienes pedidos registrados.")

            } else {

                // Permite consultar cambios de estado del gerente.
                Button(
                    onClick = {
                        pedidoViewModel.cargarPedidos()
                    }
                ) {
                    Text("Actualizar estados")
                }

                LazyColumn(
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    items(
                        items = pedidos,
                        key = { it.numero }
                    ) { pedido ->

                        TarjetaPedido(pedido = pedido)
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaPedido(
    pedido: PedidoModel
) {

    var mostrarDetalle by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = pedido.numero,
                style = MaterialTheme.typography.titleMedium
            )

            // No presenta como definitivo un total sin tarifa.
            if (
                pedido.tipoEntrega == "Domicilio" &&
                pedido.costoDespacho == null
            ) {

                Text("Total productos: $${pedido.total}")
                Text("Despacho: por confirmar")
                Text("Total final: pendiente")

            } else {

                Text("Total del pedido: $${pedido.total}")
            }

            // Estado destacado para facilitar el seguimiento.
            Text(
                text = "Estado: ${pedido.estado}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val descripcionEstado = when (pedido.estado) {

                "Confirmado" ->
                    "Recibimos tu pedido."

                "Preparando" ->
                    "Estamos preparando tus productos."

                "Listo" ->
                    if (pedido.tipoEntrega == "Domicilio") {
                        "Tu pedido esta listo para despacho."
                    } else {
                        "Puedes retirar tu pedido en tienda."
                    }

                "En despacho" ->
                    "Tu pedido va en camino."

                "Entregado" ->
                    "Tu pedido fue entregado."

                else ->
                    "Consulta el estado con el minimarket."
            }

            Text(descripcionEstado)

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

                Text(
                    text = "Informacion de entrega",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("Entrega: ${pedido.tipoEntrega}")

                if (
                    pedido.tipoEntrega == "Domicilio" &&
                    pedido.direccion.isNotBlank()
                ) {
                    Text("Direccion: ${pedido.direccion}")
                }

                Text("Metodo de pago: ${pedido.metodoPago}")

                if (pedido.tipoEntrega == "Domicilio") {

                    Text(
                        text = if (pedido.costoDespacho == null) {
                            "Costo de despacho: por confirmar"
                        } else {
                            "Costo de despacho: " +
                                    "$${pedido.costoDespacho}"
                        }
                    )
                }

                Text(
                    text = "Productos del pedido",
                    style = MaterialTheme.typography.titleMedium
                )

                for (producto in pedido.productos) {

                    Text(producto.nombre)

                    Text(
                        "Cantidad: ${producto.cantidad}"
                    )

                    Text(
                        "Subtotal: " +
                                "$${producto.precio * producto.cantidad}"
                    )
                }
            }
        }
    }
}
