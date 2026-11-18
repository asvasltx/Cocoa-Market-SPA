
package com.example.cocoa_market_spa.catalogo

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun DetalleProductoScreen(
    nombre: String,
    precio: Int,
    descripcion: String,
    stock: Int,
    imagen: Int,
    onVolver: () -> Unit,
    onAgregarCarrito: (Int) -> Unit
) {

    // Cantidad que selecciona el comprador
    // Se reinicia si cambia el producto o su stock
    var cantidad by remember(nombre, stock) {
        mutableStateOf(1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            text = "Detalles del producto",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Imagen del producto
        Image(
            painter = painterResource(id = imagen),
            contentDescription = nombre,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre y precio
        Text(
            text = nombre,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "$$precio",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Descripcion del producto
        Text(
            text = "Descripcion",
            style = MaterialTheme.typography.titleMedium
        )

        Text(text = descripcion)

        Spacer(modifier = Modifier.height(16.dp))

        // Muestra si el producto esta agotado
        if (stock <= 0) {

            Text(
                text = "Producto agotado",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium
            )

        } else {

            Text(
                text = "Stock disponible: $stock unidades",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Selecciona la cantidad",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botones para seleccionar unidades
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = {
                        if (cantidad > 1) {
                            cantidad--
                        }
                    },
                    enabled = cantidad > 1
                ) {
                    Text("-")
                }

                Text(
                    text = "   $cantidad   ",
                    style = MaterialTheme.typography.titleLarge
                )

                Button(
                    onClick = {
                        if (cantidad < stock) {
                            cantidad++
                        }
                    },
                    enabled = cantidad < stock
                ) {
                    Text("+")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Envia la cantidad al ViewModel del carrito
        // La vibracion ocurrira solo si el guardado fue aceptado
        Button(
            onClick = {
                if (stock > 0 &&
                    cantidad >= 1 &&
                    cantidad <= stock
                ) {
                    onAgregarCarrito(cantidad)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = stock > 0
        ) {
            Text("Agregar al carrito")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Regresa al catalogo
        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al catalogo")
        }
    }
}
