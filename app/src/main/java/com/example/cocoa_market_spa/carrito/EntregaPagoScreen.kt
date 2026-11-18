
package com.example.cocoa_market_spa.carrito

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EntregaPagoScreen(
    total: Int,
    guardandoPedido: Boolean,
    errorPedido: String,
    onConfirmarPedido: (String, String, String) -> Unit,
    onVolverCarrito: () -> Unit
) {

    // Opciones seleccionadas por el comprador
    var tipoEntrega by remember {
        mutableStateOf("Retiro en tienda")
    }

    var metodoPago by remember {
        mutableStateOf("Efectivo")
    }

    var direccion by remember {
        mutableStateOf("")
    }

    // Para despacho, la direccion debe tener al menos 8 caracteres
    val direccionValida =
        tipoEntrega != "Domicilio" ||
                direccion.trim().length >= 8

    // Evita confirmar una compra sin datos validos
    val puedeConfirmar =
        total > 0 &&
                direccionValida &&
                !guardandoPedido

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Entrega y pago",
                style = MaterialTheme.typography.headlineMedium
            )

            // Muestra el total antes de confirmar
            Text(
                text = "Total del pedido: $$total",
                style = MaterialTheme.typography.titleMedium
            )

            // Opciones de entrega
            Text(
                text = "Tipo de entrega",
                style = MaterialTheme.typography.titleMedium
            )

            Row {
                RadioButton(
                    selected = tipoEntrega == "Retiro en tienda",
                    onClick = {
                        tipoEntrega = "Retiro en tienda"
                    }
                )

                Text(
                    text = "Retiro en tienda",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Row {
                RadioButton(
                    selected = tipoEntrega == "Domicilio",
                    onClick = {
                        tipoEntrega = "Domicilio"
                    }
                )

                Text(
                    text = "Despacho a domicilio",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            // Pide direccion solamente si es despacho
            if (tipoEntrega == "Domicilio") {

                OutlinedTextField(
                    value = direccion,
                    onValueChange = {
                        direccion = it
                    },
                    label = {
                        Text("Direccion de entrega")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = !direccionValida
                )

                if (!direccionValida) {
                    Text(
                        text = "Ingresa una direccion de al menos 8 caracteres.",
                        color = MaterialTheme.colorScheme.error
                    )
                }

            } else {
                Text("El pedido sera retirado en el local.")
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Opciones del metodo de pago
            Text(
                text = "Metodo de pago",
                style = MaterialTheme.typography.titleMedium
            )

            Row {
                RadioButton(
                    selected = metodoPago == "Efectivo",
                    onClick = {
                        metodoPago = "Efectivo"
                    }
                )

                Text(
                    text = "Efectivo",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Row {
                RadioButton(
                    selected = metodoPago == "Tarjeta",
                    onClick = {
                        metodoPago = "Tarjeta"
                    }
                )

                Text(
                    text = "Tarjeta",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Text(
                text = "El pago se realiza al retirar o recibir el pedido.",
                style = MaterialTheme.typography.bodySmall
            )

            // Muestra un error si falla el guardado
            if (errorPedido.isNotEmpty()) {
                Text(
                    text = errorPedido,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Confirma solamente cuando los datos son validos
            Button(
                onClick = {
                    onConfirmarPedido(
                        tipoEntrega,
                        metodoPago,
                        if (tipoEntrega == "Domicilio") {
                            direccion.trim()
                        } else {
                            ""
                        }
                    )
                },
                enabled = puedeConfirmar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (guardandoPedido) {
                        "Guardando..."
                    } else {
                        "Confirmar pedido"
                    }
                )
            }

            // Regresa al carrito sin eliminar los productos
            TextButton(
                onClick = onVolverCarrito,
                enabled = !guardandoPedido,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al carrito")
            }
        }
    }
}
