
package com.example.cocoa_market_spa.gerente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GerenteScreen(
    nombreGerente: String,
    onCerrarSesion: () -> Unit
) {

    // Colores de Cocoa Market
    val cafe = Color(0xFF6D4C41)
    val crema = Color(0xFFFFF8F0)

    // Guarda la seccion que selecciona el gerente
    var seccionActual by remember {
        mutableStateOf("INICIO")
    }

    // Organiza los elementos verticalmente
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(crema)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // Nombre de la aplicacion
        Text(
            text = "Cocoa Market",
            style = MaterialTheme.typography.headlineMedium,
            color = cafe
        )

        // Mostramos el menu principal
        if (seccionActual == "INICIO") {

            Text(
                text = "Panel del Gerente",
                style = MaterialTheme.typography.titleLarge,
                color = cafe
            )

            Text(
                text = "Bienvenido, $nombreGerente",
                color = cafe
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Selecciona una opcion:",
                style = MaterialTheme.typography.titleMedium,
                color = cafe
            )

            // Boton para productos
            Button(
                onClick = {
                    seccionActual = "PRODUCTOS"
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cafe
                )
            ) {
                Text("Productos e inventario")
            }

            // Boton para pedidos
            Button(
                onClick = {
                    seccionActual = "PEDIDOS"
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cafe
                )
            ) {
                Text("Gestion de pedidos")
            }

            // Boton para reportes
            Button(
                onClick = {
                    seccionActual = "REPORTES"
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cafe
                )
            ) {
                Text("Reportes y estadisticas")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Regresa al inicio de sesion
            OutlinedButton(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cerrar sesion",
                    color = cafe
                )
            }

        } else {

            // Titulo segun la opcion seleccionada
            val titulo = when (seccionActual) {
                "PRODUCTOS" -> "Productos e inventario"
                "PEDIDOS" -> "Gestion de pedidos"
                "REPORTES" -> "Reportes y estadisticas"
                else -> "Panel del Gerente"
            }

            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                color = cafe
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido de cada seccion
            when (seccionActual) {

                "PRODUCTOS" -> {
                    Text(
                        text = "Aqui administraremos los productos, precios y stock.",
                        color = cafe
                    )
                }

                "PEDIDOS" -> {
                    Text(
                        text = "Aqui revisaremos los pedidos de nuestros clientes.",
                        color = cafe
                    )
                }

                "REPORTES" -> {
                    Text(
                        text = "Aqui consultaremos las estadisticas del negocio.",
                        color = cafe
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Boton para regresar al panel principal
            Button(
                onClick = {
                    seccionActual = "INICIO"
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cafe
                )
            ) {
                Text("Volver al panel")
            }
        }
    }
}
