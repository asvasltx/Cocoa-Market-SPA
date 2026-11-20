
package com.example.cocoa_market_spa.carrito

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cocoa_market_spa.model.PedidoModel
import com.example.cocoa_market_spa.viewmodel.PedidoViewModel
import java.text.NumberFormat
import java.util.Locale

// COLORES DE COCOA MARKET
private val CafePedidos = Color(0xFF603B2D)
private val CremaPedidos = Color(0xFFFCF8F3)
private val VerdePedidos = Color(0xFF286A43)
private val BeigePedidos = Color(0xFFF3E8E1)
private val GrisPedidos = Color(0xFF807A75)
private val BordePedidos = Color(0xFFE9E2DC)

private fun formatoPrecio(valor: Int): String {
    return "$" + NumberFormat.getIntegerInstance(
        Locale("es", "CL")
    ).format(valor)
}

@Composable
fun PedidoScreen(
    onVolvercarrito: () -> Unit,
    pedidoViewModel: PedidoViewModel
) {
    val pedidos = pedidoViewModel.pedidos.value

    // CONSERVAMOS LA CARGA DEL HISTORIAL
    LaunchedEffect(Unit) {
        pedidoViewModel.cargarPedidos()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CremaPedidos
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ENCABEZADO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
            ) {
                IconButton(
                    onClick = onVolvercarrito,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver al carrito",
                        tint = CafePedidos
                    )
                }

                Text(
                    text = "Mis pedidos",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CafePedidos,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            HorizontalDivider(
                color = BordePedidos,
                thickness = 1.dp
            )


            if (pedidoViewModel.cargandoPedidos.value) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = VerdePedidos
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Cargando pedidos...",
                            color = GrisPedidos,
                            fontSize = 13.sp
                        )
                    }
                }

            } else if (
                pedidoViewModel.errorCarga.value.isNotEmpty()
            ) {


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = pedidoViewModel.errorCarga.value,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            pedidoViewModel.cargarPedidos()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CafePedidos
                        )
                    ) {
                        Text("Reintentar")
                    }
                }

            } else if (pedidos.isEmpty()) {


                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .background(
                                    BeigePedidos,
                                    RoundedCornerShape(22.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = "Pedidos",
                                tint = CafePedidos,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            text = "Tus pedidos, en un solo lugar",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CafePedidos,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Aquí podrás revisar el estado y el historial de tus compras.",
                            fontSize = 12.sp,
                            color = GrisPedidos,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(Modifier.height(26.dp))

                        Button(
                            onClick = onVolvercarrito,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CafePedidos
                            )
                        ) {
                            Text(
                                text = "Explorar catálogo  ›",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

            } else {

                //lista de pedudis
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "TUS COMPRAS",
                        fontSize = 10.sp,
                        color = VerdePedidos,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(Modifier.height(5.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Historial de pedidos",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CafePedidos
                        )

                        Text(
                            text = "${pedidos.size} pedidos",
                            fontSize = 11.sp,
                            color = GrisPedidos
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            pedidoViewModel.cargarPedidos()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            BordePedidos
                        )
                    ) {
                        Text(
                            text = "Actualizar estados",
                            color = CafePedidos
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    LazyColumn(
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(
                            bottom = 20.dp
                        )
                    ) {
                        items(
                            items = pedidos,
                            key = { it.numero }
                        ) { pedido ->
                            TarjetaPedido(pedido = pedido)
                        }
                    }
                }
            }
        }
    }
}

//taejeta de cada oedod
@Composable
fun TarjetaPedido(
    pedido: PedidoModel
) {
    var mostrarDetalle by remember {
        mutableStateOf(false)
    }

    val descripcionEstado = when (pedido.estado) {
        "Confirmado" -> "Recibimos tu pedido."

        "Preparando" ->
            "Estamos preparando tus productos."

        "Listo" ->
            if (pedido.tipoEntrega == "Domicilio") {
                "Tu pedido está listo para despacho."
            } else {
                "Puedes retirar tu pedido en tienda."
            }

        "En despacho" -> "Tu pedido va en camino."

        "Entregado" -> "Tu pedido fue entregado."

        else -> "Consulta el estado con el minimarket."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            BordePedidos
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(
                            BeigePedidos,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = CafePedidos
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = pedido.numero,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CafePedidos
                    )

                    Text(
                        text = pedido.tipoEntrega,
                        fontSize = 11.sp,
                        color = GrisPedidos
                    )
                }
            }

            //estado
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE5F1E6)
            ) {
                Text(
                    text = pedido.estado,
                    color = VerdePedidos,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 7.dp
                    )
                )
            }

            Text(
                text = descripcionEstado,
                color = GrisPedidos,
                fontSize = 12.sp
            )

            HorizontalDivider(color = BordePedidos)


            if (
                pedido.tipoEntrega == "Domicilio" &&
                pedido.costoDespacho == null
            ) {
                Text(
                    text = "Total productos: " +
                            formatoPrecio(pedido.total),
                    color = CafePedidos,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Despacho: por confirmar",
                    color = GrisPedidos,
                    fontSize = 12.sp
                )

                Text(
                    text = "Total final: pendiente",
                    color = CafePedidos,
                    fontSize = 12.sp
                )

            } else {
                Text(
                    text = "Total del pedido: " +
                            formatoPrecio(pedido.total),
                    color = CafePedidos,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    mostrarDetalle = !mostrarDetalle
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CafePedidos
                )
            ) {
                Text(
                    text = if (mostrarDetalle) {
                        "Ocultar detalle"
                    } else {
                        "Ver detalle"
                    }
                )
            }

            //detalles del pediod
            if (mostrarDetalle) {

                HorizontalDivider(color = BordePedidos)

                Text(
                    text = "Información de entrega",
                    fontWeight = FontWeight.Bold,
                    color = CafePedidos
                )

                Text(
                    text = "Entrega: ${pedido.tipoEntrega}",
                    fontSize = 12.sp,
                    color = GrisPedidos
                )

                if (
                    pedido.tipoEntrega == "Domicilio" &&
                    pedido.direccion.isNotBlank()
                ) {
                    Text(
                        text = "Dirección: ${pedido.direccion}",
                        fontSize = 12.sp,
                        color = GrisPedidos
                    )
                }

                Text(
                    text = "Método de pago: ${pedido.metodoPago}",
                    fontSize = 12.sp,
                    color = GrisPedidos
                )

                if (pedido.tipoEntrega == "Domicilio") {
                    Text(
                        text = if (pedido.costoDespacho == null) {
                            "Costo de despacho: por confirmar"
                        } else {
                            "Costo de despacho: " +
                                    formatoPrecio(pedido.costoDespacho)
                        },
                        fontSize = 12.sp,
                        color = GrisPedidos
                    )
                }

                HorizontalDivider(color = BordePedidos)

                Text(
                    text = "Productos del pedido",
                    fontWeight = FontWeight.Bold,
                    color = CafePedidos
                )

                for (producto in pedido.productos) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                CremaPedidos,
                                RoundedCornerShape(9.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Text(
                            text = producto.nombre,
                            fontWeight = FontWeight.Medium,
                            color = CafePedidos
                        )

                        Text(
                            text = "Cantidad: ${producto.cantidad}",
                            fontSize = 12.sp,
                            color = GrisPedidos
                        )

                        Text(
                            text = "Subtotal: " +
                                    formatoPrecio(
                                        producto.precio * producto.cantidad
                                    ),
                            fontSize = 12.sp,
                            color = CafePedidos
                        )
                    }
                }
            }
        }
    }
}
