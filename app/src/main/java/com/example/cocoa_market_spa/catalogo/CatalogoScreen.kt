
package com.example.cocoa_market_spa.catalogo

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cocoa_market_spa.R
import com.example.cocoa_market_spa.data.local.entity.ProductoEntity

// colores que se ocupan
private val Cafe = Color(0xFF603B2D)
private val Crema = Color(0xFFFCF8F3)
private val Verde = Color(0xFF286A43)
private val VerdeClaro = Color(0xFFE5F1E6)
private val Gris = Color(0xFF807A75)

@Composable
fun CatalogoScreen(
    productos: List<ProductoEntity>,
    onProductoClick: (Int) -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember {
        mutableStateOf("Todos")
    }
    var soloDisponibles by remember {
        mutableStateOf(false)
    }
    var ordenPrecio by remember {
        mutableStateOf("Normal")
    }

    val context = LocalContext.current

    // categorias de la bd
    val categorias = listOf("Todos") +
            productos.map {
                it.categoria.trim()
            }.filter {
                it.isNotEmpty()
            }.distinct().sorted()

    LaunchedEffect(categorias) {
        if (categoriaSeleccionada !in categorias) {
            categoriaSeleccionada = "Todos"
        }
    }


    val productosFiltrados = productos.filter { producto ->
        val coincideCategoria =
            categoriaSeleccionada == "Todos" ||
                    producto.categoria.equals(
                        categoriaSeleccionada,
                        ignoreCase = true
                    )

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

        val coincideStock =
            !soloDisponibles || producto.stock > 0

        coincideCategoria &&
                coincideBusqueda &&
                coincideStock
    }


    val productosOrdenados = when (ordenPrecio) {
        "Menor precio" ->
            productosFiltrados.sortedBy { it.precio }

        "Mayor precio" ->
            productosFiltrados.sortedByDescending { it.precio }

        else ->
            productosFiltrados.sortedBy {
                it.nombre.lowercase()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Crema)
    ) {
        // encabezado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 12.dp,
                    bottom = 8.dp
                )
        ) {
            Row(
                modifier = Modifier
                    .clickable {
                        try {
                            val direccion =
                                "Av. Las Condes 9280, Local 4, Las Condes, Santiago, Chile"

                            val enlace =
                                "https://www.google.com/maps/search/?api=1&query=" +
                                        Uri.encode(direccion)

                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(enlace)
                                )
                            )
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "No se pudo abrir la ubicación.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ubicación",
                    tint = Verde,
                    modifier = Modifier.size(15.dp)
                )

                Text(
                    text = "Las Condes, Santiago",
                    fontSize = 11.sp,
                    color = Verde,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Cocoa Market",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Cafe
            )

            Spacer(Modifier.height(12.dp))

            // buscador
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                placeholder = {
                    Text(
                        "¿Qué necesitas hoy?",
                        fontSize = 13.sp,
                        color = Gris
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_busqueda
                        ),
                        contentDescription = "Buscar",
                        tint = Gris,
                        modifier = Modifier.size(19.dp)
                    )
                },
                trailingIcon = {
                    if (busqueda.isNotEmpty()) {
                        Text(
                            text = "✕",
                            color = Gris,
                            modifier = Modifier
                                .clickable { busqueda = "" }
                                .padding(10.dp)
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Verde,
                    unfocusedBorderColor = Color(0xFFE9E2DC)
                )
            )
        }


        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // banner
            item(span = {
                androidx.compose.foundation.lazy.grid.GridItemSpan(
                    maxLineSpan
                )
            }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(142.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Verde)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(17.dp)
                            .fillMaxWidth(0.73f)
                    ) {
                        Text(
                            text = "DESCUENTOS DE BARRIO",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(9.dp))

                        Text(
                            text = "Todo lo esencial,\na minutos de ti.",
                            color = Color.White,
                            fontSize = 20.sp,
                            lineHeight = 23.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(7.dp))

                        Text(
                            text = "Despacho local en Las Condes",
                            color = Color(0xFFE2F1E3),
                            fontSize = 10.sp
                        )
                    }

                    Text(
                        text = "🛍️",
                        fontSize = 66.sp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 9.dp, bottom = 4.dp)
                    )
                }
            }

            // titulo de las categorias
            item(span = {
                androidx.compose.foundation.lazy.grid.GridItemSpan(
                    maxLineSpan
                )
            }) {
                Column {
                    Text(
                        text = "COMPRA RÁPIDO",
                        fontSize = 10.sp,
                        color = Verde,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(5.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Categorías",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Cafe
                        )

                        Text(
                            text = "Ver todo",
                            fontSize = 12.sp,
                            color = Cafe,
                            modifier = Modifier.clickable {
                                categoriaSeleccionada = "Todos"
                            }
                        )
                    }
                }
            }

            //botones de las categorias
            item(span = {
                androidx.compose.foundation.lazy.grid.GridItemSpan(
                    maxLineSpan
                )
            }) {
                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(7.dp)
                ) {
                    items(categorias) { categoria ->
                        val seleccionada =
                            categoriaSeleccionada == categoria

                        Surface(
                            modifier = Modifier.clickable {
                                categoriaSeleccionada = categoria
                            },
                            shape = RoundedCornerShape(50.dp),
                            color = if (seleccionada) {
                                Cafe
                            } else {
                                Color.White
                            },
                            border = if (seleccionada) {
                                null
                            } else {
                                androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color(0xFFE9E2DC)
                                )
                            }
                        ) {
                            Text(
                                text = categoria,
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 10.dp
                                ),
                                color = if (seleccionada) {
                                    Color.White
                                } else {
                                    Cafe
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            //filtros
            item(span = {
                androidx.compose.foundation.lazy.grid.GridItemSpan(
                    maxLineSpan
                )
            }) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = soloDisponibles,
                            onCheckedChange = {
                                soloDisponibles = it
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Verde
                            ),
                            modifier = Modifier.size(36.dp)
                        )

                        Text(
                            text = "Solo disponibles",
                            fontSize = 12.sp,
                            color = Cafe
                        )

                        Spacer(Modifier.weight(1f))

                        TextButton(
                            onClick = {
                                ordenPrecio = when (ordenPrecio) {
                                    "Normal" -> "Menor precio"
                                    "Menor precio" -> "Mayor precio"
                                    else -> "Normal"
                                }
                            }
                        ) {
                            Text(
                                text = "Orden: $ordenPrecio",
                                fontSize = 11.sp,
                                color = Verde
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Favoritos del barrio",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Cafe
                        )

                        Text(
                            text = "${productosOrdenados.size} productos",
                            fontSize = 11.sp,
                            color = Gris
                        )
                    }
                }
            }


            if (productosOrdenados.isEmpty()) {
                item(span = {
                    androidx.compose.foundation.lazy.grid.GridItemSpan(
                        maxLineSpan
                    )
                }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (productos.isEmpty()) {
                                "No hay productos registrados."
                            } else {
                                "No encontramos productos con esos filtros."
                            },
                            color = Gris
                        )

                        if (productos.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    busqueda = ""
                                    categoriaSeleccionada = "Todos"
                                    soloDisponibles = false
                                    ordenPrecio = "Normal"
                                }
                            ) {
                                Text(
                                    "Limpiar filtros",
                                    color = Verde
                                )
                            }
                        }
                    }
                }
            } else {
                // tarjeyas en dos columnas
                items(
                    items = productosOrdenados,
                    key = { it.id }
                ) { producto ->
                    Producto(
                        nombre = producto.nombre,
                        precio = "$${producto.precio}",
                        imagen = producto.imagen,
                        stock = producto.stock,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onProductoClick(producto.id)
                        }
                    )
                }
            }
        }
    }
}

// tarjeta del producto
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
            // Conserva la navegación al detalle
            onClick()
        },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(9.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Crema)
            ) {
                Image(
                    painter = painterResource(id = imagen),
                    contentDescription = nombre,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(5.dp),
                    shape = RoundedCornerShape(5.dp),
                    color = Color.White.copy(alpha = 0.92f)
                ) {
                    Text(
                        text = if (stock > 0) {
                            "LOCAL"
                        } else {
                            "AGOTADO"
                        },
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (stock > 0) Verde else Color.Red,
                        modifier = Modifier.padding(
                            horizontal = 6.dp,
                            vertical = 4.dp
                        )
                    )
                }
            }

            Spacer(Modifier.height(9.dp))

            Text(
                text = nombre,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Cafe,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = precio,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Cafe
            )

            Spacer(Modifier.height(10.dp))

            // Botón visual que abre el detalle del producto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(9.dp))
                    .background(
                        if (stock > 0) {
                            VerdeClaro
                        } else {
                            Color(0xFFF5E7E4)
                        }
                    )
                    .clickable { onClick() }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (stock > 0) {
                        "✓  Ver producto"
                    } else {
                        "Ver agotado"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (stock > 0) {
                        Verde
                    } else {
                        Color(0xFFB05245)
                    }
                )
            }
        }
    }
}
