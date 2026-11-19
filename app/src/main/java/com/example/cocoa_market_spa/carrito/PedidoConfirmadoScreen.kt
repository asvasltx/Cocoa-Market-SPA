
package com.example.cocoa_market_spa.carrito

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PedidoConfirmadoScreen(
    total: Int,
    tipoEntrega: String,
    metodoPago: String,
    direccion: String,
    costoDespacho: Int?,
    onVerPedidos: () -> Unit,
    onVolverCatalogo: () -> Unit
) {

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "¡Pedido confirmado!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("El pedido se guardo correctamente.")

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Resumen de compra",
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (tipoEntrega == "Domicilio" &&
                        costoDespacho == null
                    ) {

                        Text("Total productos: $$total")
                        Text("Despacho: por confirmar")
                        Text("Total final: pendiente")

                    } else {

                        val subtotal = total - (costoDespacho ?: 0)

                        Text("Productos: $$subtotal")
                        Text("Despacho: $${costoDespacho ?: 0}")
                        Text("Total: $$total")
                    }

                    Text("Estado: Confirmado")

                    Text("Entrega: $tipoEntrega")

                    if (tipoEntrega == "Domicilio") {
                        Text("Direccion: $direccion")
                    }

                    Text("Metodo de pago: $metodoPago")

                    Text(
                        text = "El pago todavia no se ha realizado.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onVerPedidos,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver mis pedidos")
            }

            OutlinedButton(
                onClick = onVolverCatalogo,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al catalogo")
            }
        }
    }
}
