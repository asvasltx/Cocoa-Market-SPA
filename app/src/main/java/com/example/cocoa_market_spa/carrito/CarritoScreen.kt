
package com.example.cocoa_market_spa.carrito

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cocoa_market_spa.viewmodel.CarritoViewModel
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

//colores
private val Cafe = Color(0xFF603B2D)
private val Crema = Color(0xFFFCF8F3)
private val Verde = Color(0xFF286A43)
private val Beige = Color(0xFFF3E8E1)
private val Gris = Color(0xFF807A75)
private val Borde = Color(0xFFE9E2DC)


private fun precioCLP(valor: Int): String {
    val formato = NumberFormat.getIntegerInstance(
        Locale("es", "CL")
    )
    return "$" + formato.format(valor)
}

@Composable
fun CarritoScreen(
    onNavigateToPedidos: () -> Unit,
    onVolverCatalogo: () -> Unit,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    //datos reales de carrito
    val productosViewModel = carritoViewModel.productos.value
    val total = carritoViewModel.calcularTotal()
    val context = LocalContext.current

    val cantidadProductos = productosViewModel.sumOf {
        it.cantidad
    }

    //consulta stock
    LaunchedEffect(Unit) {
        carritoViewModel.actualizarStockDisponible()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Crema
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            //encabezado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .background(Crema)
            ) {

                IconButton(
                    onClick = onVolverCatalogo,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver al catálogo",
                        tint = Cafe
                    )
                }

                Text(
                    text = "Tu carrito",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Cafe,
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }

            HorizontalDivider(
                color = Borde,
                thickness = 1.dp
            )

            //contenido del carrito
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 18.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {


                item {
                    Column {
                        Text(
                            text = "CASI LISTO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Verde,
                            letterSpacing = 1.sp
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            text = if (cantidadProductos == 1) {
                                "1 producto en tu pedido"
                            } else {
                                "$cantidadProductos productos en tu pedido"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Cafe
                        )
                    }
                }

                if (carritoViewModel.mensaje.value.isNotEmpty()) {
                    item {
                        Text(
                            text = carritoViewModel.mensaje.value,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }

                if (productosViewModel.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(15.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Tu carrito está vacío",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Cafe
                                )

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = "Agrega productos desde el catálogo.",
                                    fontSize = 12.sp,
                                    color = Gris
                                )
                            }
                        }
                    }
                } else {

                    // productos del   carito
                    items(productosViewModel) { producto ->

                        val stock =
                            carritoViewModel.stockDisponible.value[
                                producto.productoId
                            ]

                        ProductoCarrito(
                            producto = producto.nombre,
                            precio = producto.precio,
                            cantidad = producto.cantidad,
                            stock = stock,
                            imagen = producto.imagen,
                            enProceso =
                                carritoViewModel.operacionEnCurso.value,

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


                item {
                    OutlinedButton(
                        onClick = onVolverCatalogo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            Color(0xFFD8C5B9)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Cafe
                        )
                    ) {
                        Text(
                            text = "+  Agregar más productos",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }


                if (productosViewModel.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Beige
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(17.dp)
                            ) {
                                Text(
                                    text = "Resumen del pedido",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Cafe
                                )

                                Spacer(Modifier.height(15.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Subtotal",
                                        color = Gris,
                                        fontSize = 12.sp
                                    )

                                    Text(
                                        text = precioCLP(total),
                                        color = Cafe,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Despacho estimado",
                                        color = Gris,
                                        fontSize = 12.sp
                                    )

                                    Text(
                                        text = "Por calcular",
                                        color = Cafe,
                                        fontSize = 12.sp
                                    )
                                }

                                Spacer(Modifier.height(15.dp))

                                HorizontalDivider(
                                    color = Color(0xFFDCCBC1)
                                )

                                Spacer(Modifier.height(13.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Subtotal actual",
                                        color = Cafe,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = precioCLP(total),
                                        color = Cafe,
                                        fontSize = 23.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(Modifier.height(9.dp))

                                Text(
                                    text = "El valor del despacho se confirmará más adelante.",
                                    color = Gris,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            //boton de abajo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Crema)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 12.dp,
                        top = 8.dp
                    )
            ) {
                Button(
                    onClick = {
                        Toast.makeText(
                            context,
                            "Continuar al pedido",
                            Toast.LENGTH_SHORT
                        ).show()

                        onNavigateToPedidos()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    enabled =
                        carritoViewModel.carritoValido() &&
                                !carritoViewModel.operacionEnCurso.value,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Cafe,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Continuar con el pedido   ›",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

//tarjeta producto carrito
@Composable
fun ProductoCarrito(
    producto: String,
    precio: Int,
    imagen: Int,
    cantidad: Int,
    stock: Int?,
    enProceso: Boolean,
    onAumentar: () -> Unit,
    onDisminuir: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            Borde
        )
    ) {
        Column(
            modifier = Modifier.padding(13.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {


                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .background(
                            Crema,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = imagen),
                        contentDescription = producto,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = producto,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Cafe,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Stock: " +
                                (stock?.toString() ?: "Consultando..."),
                        fontSize = 10.sp,
                        color = Gris
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = precioCLP(precio) + " c/u",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Cafe
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                //eliminar producto
                Text(
                    text = "Eliminar",
                    fontSize = 11.sp,
                    color = Color(0xFFB05245),
                    modifier = Modifier
                        .clickable(enabled = !enProceso) {
                            onEliminar()
                        }
                        .padding(8.dp)
                )

                Spacer(Modifier.weight(1f))

                //disminuir cantidad
                OutlinedButton(
                    onClick = onDisminuir,
                    enabled = cantidad > 1 && !enProceso,
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(9.dp),
                    border = BorderStroke(1.dp, Borde)
                ) {
                    Text(
                        text = "−",
                        color = Cafe,
                        fontSize = 17.sp
                    )
                }

                Text(
                    text = "$cantidad",
                    modifier = Modifier.padding(
                        horizontal = 13.dp
                    ),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Cafe
                )

                //aumemtar cantiadad
                OutlinedButton(
                    onClick = onAumentar,
                    enabled =
                        stock != null &&
                                cantidad < stock &&
                                !enProceso,
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(9.dp),
                    border = BorderStroke(1.dp, Borde)
                ) {
                    Text(
                        text = "+",
                        color = Cafe,
                        fontSize = 17.sp
                    )
                }
            }

            // aviso stock
            if (stock != null && cantidad > stock) {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Stock insuficiente. Ajusta la cantidad.",
                    fontSize = 11.sp,
                    color = Color(0xFFB05245)
                )
            }
        }
    }
}
