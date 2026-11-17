package com.example.cocoa_market_spa.gerente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GerenteScreen(
    nombreGerente: String,
    onCerrarSesion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Panel del Gerente",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Bienvenido, $nombreGerente")

        Spacer(modifier = Modifier.height(24.dp))

        Text("Gestion de productos")
        Text("Gestion de pedidos")
        Text("Reportes y estadisticas")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onCerrarSesion) {
            Text("Cerrar sesion")
        }
    }
}
