
package com.example.cocoa_market_spa.catalogo

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cocoa_market_spa.R
import com.example.cocoa_market_spa.data.local.entity.ProductoEntity

@Composable
fun CatalogoScreen(
    productos: List<ProductoEntity>,
    onProductoClick: (Int) -> Unit
) {

    // Guarda el texto que escribe el comprador
    var busqueda by remember {
        mutableStateOf("")
    }

    // Categoria seleccionada
    var categoriaSeleccionada by remember {
        mutableStateOf("Todos")
    }

    // Permite mostrar solamente productos con stock
    var soloDisponibles by remember {
        mutableStateOf(false)
    }

    // Controla el orden de los precios
    var ordenPrecio by remember {
        mutableStateOf("Normal")
    }

    val context = LocalContext.current

    // Obtiene las categorias de los productos registrados.
    // Asi tambien apareceran las categorias de nuevos productos.
    val categorias = listOf("Todos") +
            productos.map {
                it.categoria.trim()
            }.filter {
                it.isNotEmpty()
            }.distinct().sorted()

    // Si se elimina una categoria, vuelve a Todos.
    LaunchedEffect(categorias) {
        if (categoriaSeleccionada !in categorias) {
            categoriaSeleccionada = "Todos"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Abre la ubicacion del local en Google Maps
        Row(
            modifier = Modifier.clickable {

                try {

                    val direccion =
                        "Av. Las Condes 9280, Local 4, Las Condes, Santiago, Chile"

                    val enlace =
                        "https://www.google.com/maps/search/?api=1&query=" +
                                Uri.encode(direccion)

                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(enlace)
                    )

                    context.startActivity(intent)

                } catch (e: Exception) {

                    Toast.makeText(
                        context,
                        "No se pudo abrir la ubicacion.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Ubicacion"
            )

            Text(
                text = "Ubicanos :D",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Cocoa Market",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // BUSQUEDA POR NOMBRE O DESCRIPCION
        OutlinedTextField(
            value = busqueda,
            onValueChange = {
                busqueda = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("¿Que producto buscas?")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_busqueda
                    ),
                    contentDescription = "Buscar",
                    modifier = Modifier.size(20.dp)
                )
            },
            singleLine = true
        )

        // Permite limpiar rapidamente la busqueda
        if (busqueda.isNotEmpty()) {

            TextButton(
                onClick = {
                    busqueda = ""
                }
            ) {
                Text("Limpiar busqueda")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Catalogo de productos",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // FILTROS DE CATEGORIA
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {

            items(categorias) { categoria ->

                Button(
                    onClick = {
                        categoriaSeleccionada = categoria
                    }
                ) {
                    Text(categoria)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // FILTRO DE PRODUCTOS DISPONIBLES
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = soloDisponibles,
                onCheckedChange = {
                    soloDisponibles = it
                }
            )

            Text("Solo productos disponibles")
        }

        // ORDENAMIENTO POR PRECIO
        Button(
            onClick = {

                ordenPrecio = when (ordenPrecio) {

                    "Normal" -> "Menor precio"

                    "Menor precio" -> "Mayor precio"

                    else -> "Normal"
                }
            }
        ) {

            Text("Orden: $ordenPrecio")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Conserva la animacion al cambiar de categoria
        AnimatedContent(
            targetState = categoriaSeleccionada,
            label = "AnimacionDeCatalogo",
            modifier = Modifier.weight(1f)
        ) { categoriaActual ->

            // BUSQUEDA Y FILTROS
            val productosFiltrados = productos.filter { producto ->

                // Comprueba la categoria
                val coincideCategoria =
                    categoriaActual == "Todos" ||
                            producto.categoria.equals(
                                categoriaActual,
                                ignoreCase = true
                            )

                // Busca por nombre o descripcion
                val texto = busqueda.trim()

                val coincideBusqueda =
                    producto.nombre.contains(
                        texto,
                        ignoreCase = true
                    ) ||
                            producto.descripcion.contains(
                                texto,
                                ignoreCase = true
                            )

                // Comprueba disponibilidad
                val coincideStock =
                    !soloDisponibles || producto.stock > 0

                // Deben cumplirse todas las condiciones
                coincideCategoria &&
                        coincideBusqueda &&
                        coincideStock

            }

            // Aplica el orden seleccionado
            val productosOrdenados = when (ordenPrecio) {

                "Menor precio" -> {
                    productosFiltrados.sortedBy {
                        it.precio
                    }
                }

                "Mayor precio" -> {
                    productosFiltrados.sortedByDescending {
                        it.precio
                    }
                }

                else -> {
                    productosFiltrados.sortedBy {
                        it.nombre.lowercase()
                    }
                }
            }

            // Muestra un mensaje cuando no existen resultados
            if (productosOrdenados.isEmpty()) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = if (productos.isEmpty()) {
                            "No hay productos registrados."
                        } else {
                            "No encontramos productos con esos filtros."
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (productos.isNotEmpty()) {

                        // Restablece todos los filtros
                        Button(
                            onClick = {
                                busqueda = ""
                                categoriaSeleccionada = "Todos"
                                soloDisponibles = false
                                ordenPrecio = "Normal"
                            }
                        ) {
                            Text("Limpiar filtros")
                        }
                    }
                }

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(productosOrdenados) { producto ->

                        Producto(
                            nombre = producto.nombre,
                            precio = "$${producto.precio}",
                            imagen = producto.imagen,
                            stock = producto.stock,
                            onClick = {
                                onProductoClick(producto.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

// Tarjeta de producto del catalogo
@Composable
fun Producto(
    nombre: String,
    precio: String,
    imagen: Int,
    stock: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier.clickable {
            // Permite consultar el detalle aunque este agotado
            onClick()
        },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            // Imagen del producto
            Image(
                painter = painterResource(id = imagen),
                contentDescription = nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre del producto
            Text(
                text = nombre,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Precio en pesos chilenos
            Text(
                text = precio,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Informa sobre el inventario
            if (stock <= 0) {

                Text(
                    text = "Producto agotado",
                    color = MaterialTheme.colorScheme.error
                )

            } else {

                Text(
                    text = "Disponible: $stock unidades",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
