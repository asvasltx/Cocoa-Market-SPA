
package com.example.cocoa_market_spa.catalogo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

private val CafeDetalle = Color(0xFF603B2D)
private val CremaDetalle = Color(0xFFFCF8F3)
private val BeigeDetalle = Color(0xFFF3E8E1)
private val VerdeDetalle = Color(0xFF286A43)
private val GrisDetalle = Color(0xFF807A75)
private val BordeDetalle = Color(0xFFE9E2DC)

private fun formatoPrecioDetalle(precio: Int): String {
    return "$" + NumberFormat.getIntegerInstance(
        Locale("es", "CL")
    ).format(precio)
}

@Composable
fun DetalleProductoScreen(
    nombre: String,
    precio: Int,
    descripcion: String,
    stock: Int,
    imagen: Int,
    onVolver: () -> Unit,
    onAgregarCarrito: (Int) -> Unit
) {
    // Se conserva la lógica original de cantidad.
    var cantidad by remember(nombre, stock) {
        mutableStateOf(1)
    }

    val agotado = stock <= 0
    val cantidadValida = !agotado &&
            cantidad >= 1 &&
            cantidad <= stock

    Scaffold(
        containerColor = CremaDetalle
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // ENCABEZADO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .background(CafeDetalle),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onVolver,
                    modifier = Modifier.align(
                        Alignment.CenterStart
                    )
                ) {
                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver al catálogo",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Detalles del producto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            // CONTENIDO
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // IMAGEN DEL PRODUCTO
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = BorderStroke(
                        1.dp,
                        BordeDetalle
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = imagen),
                            contentDescription = nombre,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // INFORMACIÓN DEL PRODUCTO
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = nombre,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = CafeDetalle
                    )

                    Text(
                        text = formatoPrecioDetalle(precio),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdeDetalle
                    )
                }

                // DESCRIPCIÓN
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = BorderStroke(
                        1.dp,
                        BordeDetalle
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Descripción",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CafeDetalle
                        )

                        Text(
                            text = descripcion,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = GrisDetalle
                        )
                    }
                }

                // DISPONIBILIDAD
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = if (agotado) {
                        Color(0xFFFFEEEE)
                    } else {
                        Color(0xFFEAF5EC)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (agotado) "●" else "✓",
                            color = if (agotado) {
                                Color(0xFFB3261E)
                            } else {
                                VerdeDetalle
                            },
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (agotado) {
                                "Producto agotado"
                            } else {
                                "Stock disponible: $stock unidades"
                            },
                            color = if (agotado) {
                                Color(0xFFB3261E)
                            } else {
                                VerdeDetalle
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // SELECTOR DE CANTIDAD
                if (!agotado) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(
                            1.dp,
                            BordeDetalle
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Selecciona la cantidad",
                                color = CafeDetalle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement.Center,
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {
                                FilledIconButton(
                                    onClick = {
                                        if (cantidad > 1) {
                                            cantidad--
                                        }
                                    },
                                    enabled = cantidad > 1,
                                    modifier = Modifier.size(44.dp),
                                    colors = IconButtonDefaults
                                        .filledIconButtonColors(
                                            containerColor = BeigeDetalle,
                                            contentColor = CafeDetalle,
                                            disabledContainerColor =
                                                Color(0xFFF1EFED),
                                            disabledContentColor =
                                                Color.LightGray
                                        )
                                ) {
                                    Text(
                                        text = "−",
                                        fontSize = 22.sp
                                    )
                                }

                                Text(
                                    text = cantidad.toString(),
                                    modifier = Modifier.width(70.dp),
                                    textAlign = TextAlign.Center,
                                    color = CafeDetalle,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                FilledIconButton(
                                    onClick = {
                                        if (cantidad < stock) {
                                            cantidad++
                                        }
                                    },
                                    enabled = cantidad < stock,
                                    modifier = Modifier.size(44.dp),
                                    colors = IconButtonDefaults
                                        .filledIconButtonColors(
                                            containerColor = CafeDetalle,
                                            contentColor = Color.White,
                                            disabledContainerColor =
                                                BeigeDetalle,
                                            disabledContentColor =
                                                GrisDetalle
                                        )
                                ) {
                                    Text(
                                        text = "+",
                                        fontSize = 22.sp
                                    )
                                }
                            }

                            HorizontalDivider(
                                color = BordeDetalle
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement.SpaceBetween,
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Subtotal",
                                    color = GrisDetalle,
                                    fontSize = 13.sp
                                )

                                Text(
                                    text = formatoPrecioDetalle(
                                        precio * cantidad
                                    ),
                                    color = CafeDetalle,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // BOTONES INFERIORES
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CremaDetalle)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 10.dp,
                        bottom = 12.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (cantidadValida) {
                            onAgregarCarrito(cantidad)
                        }
                    },
                    enabled = cantidadValida,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CafeDetalle,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (agotado) {
                            "Producto agotado"
                        } else {
                            "Agregar al carrito"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = onVolver,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        CafeDetalle
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CafeDetalle
                    )
                ) {
                    Text(
                        text = "Volver al catálogo",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
