
package com.example.cocoa_market_spa.catalogo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MiCuentaScreen(
    nombreUsuario: String,
    onVerPedidos: () -> Unit,
    onVolverCatalogo: () -> Unit,
    onCerrarSesion: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Informacion de la cuenta del comprador
        Text(
            text = "Mi cuenta",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Hola, $nombreUsuario",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Tipo de cuenta: Cliente",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Permite consultar las compras registradas
        Button(
            onClick = onVerPedidos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mis pedidos")
        }

        // Regresa a los productos del minimarket
        OutlinedButton(
            onClick = onVolverCatalogo,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al catalogo")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cierra la sesion actual
        OutlinedButton(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesion")
        }
    }
}
