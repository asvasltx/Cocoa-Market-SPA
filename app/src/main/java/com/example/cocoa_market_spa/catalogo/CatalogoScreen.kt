package com.example.cocoa_market_spa.catalogo


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CatalogoScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "cocoa market",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Catalogo de producto",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
        Producto(
            nombre = "Arroz Tucapel 1KG",
            precio = "1.290$"
        )
        Producto(
            nombre = "Leche Chocolate Colun 200ML",
            precio = "1.490"
        )
        }
        Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Producto(
                    nombre = "Aceite Maravilla 1LT",
                    precio = "2.0000$"
                )
                Producto(
                    nombre = "Pasta de dientes Pesodent",
                    precio = "1.000$"
                )
            }
        }
    }

@Composable
fun Producto(
    nombre: String,
    precio: String
){
    Card(
        modifier = Modifier.height(120.dp)
    ){
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = nombre,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            Text(
                text = precio,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
