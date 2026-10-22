package com.example.cocoa_market_spa.carrito


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EntregaPagoScreen(
    onConfirmarPedido: () -> Unit,
    onVolverCarrito: () -> Unit
) {
    var tipoEntrega by remember { mutableStateOf("Retiro en tienda") }
    var metodoPago by remember { mutableStateOf("Efectivo") }
    var direccion by remember { mutableStateOf("") }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text(
                text = "Entrega y pago",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tipo de entrega",
                style = MaterialTheme.typography.titleMedium
            )

            Row {
                RadioButton(
                    selected = tipoEntrega == "Retiro en tienda",
                    onClick = { tipoEntrega = "Retiro en tienda" }
                )
                Text(
                    text = "Retiro en tienda",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Row {
                RadioButton(
                    selected = tipoEntrega == "Domicilio",
                    onClick = { tipoEntrega = "Domicilio" }
                )
                Text(
                    text = "Despacho a domicilio",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            if (tipoEntrega == "Domicilio") {
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección de entrega") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text("Retira tu pedido en nuestra tienda")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Método de pago",
                style = MaterialTheme.typography.titleMedium
            )

            Row {
                RadioButton(
                    selected = metodoPago == "Efectivo",
                    onClick = { metodoPago = "Efectivo" }
                )
                Text(
                    text = "Efectivo",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Row {
                RadioButton(
                    selected = metodoPago == "Tarjeta",
                    onClick = { metodoPago = "Tarjeta" }
                )
                Text(
                    text = "Tarjeta",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onConfirmarPedido,
                enabled = tipoEntrega != "Domicilio" ||
                        direccion.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar pedido")
            }

            TextButton(
                onClick = onVolverCarrito,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al carrito")
            }
        }
    }
}
