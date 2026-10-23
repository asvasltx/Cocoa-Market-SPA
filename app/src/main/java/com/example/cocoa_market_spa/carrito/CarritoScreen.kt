package com.example.cocoa_market_spa.carrito

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.Arrangement


import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cocoa_market_spa.model.CarritoModel
import com.example.cocoa_market_spa.viewmodel.CarritoViewModel

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment

@Composable
fun CarritoScreen(
    onNavigateToPedidos: () -> Unit,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    val productosViewModel = carritoViewModel.productos.value



    val total = carritoViewModel.calcularTotal()

    val context = LocalContext.current

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
                text = "Mi carrito",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Productos agregados al carrito",
                style = MaterialTheme.typography.bodyMedium
            )


            Spacer(modifier = Modifier.height(16.dp))




            if (productosViewModel.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tu carrito está vacío",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(productosViewModel) { producto ->

                        ProductoCarrito(
                            producto = producto.nombre,
                            precio = producto.precio,
                            cantidad = producto.cantidad,
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

            Text(
                text = "Total del carrito: $$total",
                style = MaterialTheme.typography.titleLarge
            )


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Toast.makeText(
                        context,
                        "continuar al pago",
                        Toast.LENGTH_SHORT
                    ).show()
                    onNavigateToPedidos()

                },
                modifier = Modifier.fillMaxWidth(),
                enabled = productosViewModel.isNotEmpty()
            ) {
                Text(text = "Confirmar pedido")
            }


        }

    }
}




    @Composable
    fun ProductoCarrito(
        producto: String,
        precio: Int,
        cantidad: Int,
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
                Text(
                    text = producto,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Precio: $$precio",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Subtotal: $${precio * cantidad}",
                    style = MaterialTheme.typography.titleMedium
                )


                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Button(
                        onClick = onDisminuir,
                        enabled = cantidad > 1
                    ) {
                        Text("-")
                    }


                    Text(text = "$cantidad")


                    Button(
                        onClick = onAumentar
                    ) {
                        Text("+")
                    }

                }
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onEliminar
                ) {
                    Text(text = "Eliminar producto")
                }
            }
        }
    }


