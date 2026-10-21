package com.example.cocoa_market_spa.carrito

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card

import androidx.compose.material3.Button
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun PedidoScreen(
    onVolvercarrito:()-> Unit
) {

    val pedidos = listOf(
        "Pedido N° 1" to 4780,
        "Pedido N° 2" to 3290,
        "Pedido N° 3" to 2000
    )

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


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Aquí aparecerán tus pedidos realizados",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))


            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pedidos) { pedido ->
                    TarjetaPedido(
                        numero = pedido.first,
                        total = pedido.second
                    )
                }
            }

        }
    }

}



@Composable
fun TarjetaPedido(
    numero: String,
    total: Int
) {
    var mostrarDetalle by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = numero,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total: $$total",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Estado: Confirmado",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    mostrarDetalle = !mostrarDetalle
                }
            ) {
                Text(
                    text = if (mostrarDetalle) "Ocultar detalle" else "Ver detalle"
                )
            }

            if (mostrarDetalle) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Detalle del pedido",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Aquí aparecerán los productos comprados"
                )
            }

        }
    }
}

