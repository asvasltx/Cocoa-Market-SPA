package com.example.cocoa_market_spa.catalogo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    onVolver: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Detalles del producto",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = imagen),
            contentDescription = nombre,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)

        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = nombre,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "$$precio",
            style = MaterialTheme.typography.titleMedium

        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Descripcion",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = descripcion,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Stock disponible $stock unidades",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Volver al catalogo"
            )
        }

    }
}