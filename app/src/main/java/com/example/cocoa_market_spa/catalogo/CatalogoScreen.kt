
package com.example.cocoa_market_spa.catalogo

import android.content.ClipData
import android.media.RouteListingPreference
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cocoa_market_spa.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn

@Composable
fun CatalogoScreen(
    onProductoClick: (Int) -> Unit
) {

    var categoriaSeleccionada by remember {
        mutableStateOf("Todos")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Cocoa Market",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Catálogo de productos",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Desplazamiento horizontal
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(
                listOf("Todos", "Abarrotes", "Bebidas", "Higiene")
            ) { categoria ->

                Button(
                    onClick = {
                        categoriaSeleccionada = categoria
                    }
                ) {
                    Text(text = categoria)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // transición animada para los productos seleccionados como muestra
        AnimatedContent(
            targetState = categoriaSeleccionada,
            label = "AnimacionDeCatalogo",
            modifier = Modifier.weight(1f),
        ) { categoriaActual ->

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        if (
                            categoriaActual == "Todos" ||
                            categoriaActual == "Abarrotes"
                        ) {
                            Producto(
                                nombre = "Arroz Tucapel 1 kg",
                                precio = "$1.290",
                                imagen = R.drawable.arroztucapel,
                                modifier = Modifier.weight(1f),
                                onClick = { onProductoClick(1) }
                            )
                        }

                        if (
                            categoriaActual == "Todos" ||
                            categoriaActual == "Bebidas"
                        ) {
                            Producto(
                                nombre = "Leche Chocolate Colun 200 ml",
                                precio = "$1.490",
                                imagen = R.drawable.lechechocolate,
                                modifier = Modifier.weight(1f),
                                onClick = { onProductoClick(2) }
                            )
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        if (
                            categoriaActual == "Todos" ||
                            categoriaActual == "Abarrotes"
                        ) {
                            Producto(
                                nombre = "Aceite Maravilla 1 L",
                                precio = "$2.000",
                                imagen = R.drawable.aceite,
                                modifier = Modifier.weight(1f),
                                onClick = { onProductoClick(3) }
                            )
                        }

                        if (
                            categoriaActual == "Todos" ||
                            categoriaActual == "Higiene"
                        ) {
                            Producto(
                                nombre = "Pasta de dientes Pepsodent",
                                precio = "$1.000",
                                imagen = R.drawable.pastadedientes,
                                modifier = Modifier.weight(1f),
                                onClick = { onProductoClick(4) }
                            )
                        }
                    }
                }
            }

        }

    }

}


@Composable
fun Producto(
    nombre: String,
    precio: String,
    imagen: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier.clickable{
            onClick()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            Image(
                painter = painterResource(id = imagen),
                contentDescription = nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = nombre,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = precio,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
