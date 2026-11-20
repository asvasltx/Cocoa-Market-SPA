
package com.example.cocoa_market_spa.carrito

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

//colores
private val CafeEntrega = Color(0xFF603B2D)
private val CremaEntrega = Color(0xFFFCF8F3)
private val VerdeEntrega = Color(0xFF286A43)
private val BeigeEntrega = Color(0xFFF3E8E1)
private val GrisEntrega = Color(0xFF807A75)
private val BordeEntrega = Color(0xFFE9E2DC)

private fun precioEntrega(valor: Int): String {
    return "$" + NumberFormat.getIntegerInstance(
        Locale("es", "CL")
    ).format(valor)
}

@Composable
fun EntregaPagoScreen(
    total: Int,
    guardandoPedido: Boolean,
    errorPedido: String,
    onConfirmarPedido: (String, String, String) -> Unit,
    onVolverCarrito: () -> Unit
) {

    var tipoEntrega by remember {
        mutableStateOf("Retiro en tienda")
    }

    var metodoPago by remember {
        mutableStateOf("Efectivo")
    }

    var direccion by remember {
        mutableStateOf("")
    }

    // CONSERVAMOS LA VALIDACIÓN DE DIRECCIÓN
    val direccionValida =
        tipoEntrega != "Domicilio" ||
                direccion.trim().length >= 8

    // CONSERVAMOS EL COSTO CONFIGURADO
    val costoDespacho = if (tipoEntrega == "Domicilio") {
        ConfiguracionEntrega.COSTO_DESPACHO
    } else {
        0
    }

    val puedeConfirmar =
        total > 0 &&
                direccionValida &&
                !guardandoPedido

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CremaEntrega
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onVolverCarrito,
                    enabled = !guardandoPedido,
                    modifier = Modifier.align(
                        Alignment.CenterStart
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver al carrito",
                        tint = CafeEntrega
                    )
                }

                Text(
                    text = "Entrega y pago",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CafeEntrega
                )
            }

            HorizontalDivider(
                color = BordeEntrega
            )


            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {


                Column {
                    Text(
                        text = "ÚLTIMO PASO",
                        color = VerdeEntrega,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = "Finaliza tu pedido",
                        color = CafeEntrega,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Elige cómo recibir tus productos y cómo pagar.",
                        color = GrisEntrega,
                        fontSize = 12.sp
                    )
                }

                //ti´p de entrega
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = BorderStroke(
                        1.dp,
                        BordeEntrega
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Tipo de entrega",
                            color = CafeEntrega,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        // RETIRO EN TIENDA
                        OpcionEntregaPago(
                            titulo = "Retiro en tienda",
                            descripcion = "Retira tu pedido en nuestro local.",
                            seleccionada =
                                tipoEntrega == "Retiro en tienda",
                            onClick = {
                                tipoEntrega = "Retiro en tienda"
                            }
                        )

                        //despacho a domicilio
                        OpcionEntregaPago(
                            titulo = "Despacho a domicilio",
                            descripcion = "Recibe tus productos en tu dirección.",
                            seleccionada =
                                tipoEntrega == "Domicilio",
                            onClick = {
                                tipoEntrega = "Domicilio"
                            }
                        )

                        if (tipoEntrega == "Domicilio") {

                            HorizontalDivider(
                                color = BordeEntrega
                            )

                            Text(
                                text = "Los despachos se realizan " +
                                        "${ConfiguracionEntrega.DESPACHOS_POR_SEMANA} " +
                                        "veces por semana.",
                                fontSize = 12.sp,
                                color = VerdeEntrega
                            )

                            Text(
                                text = "La fecha de entrega se informará " +
                                        "cuando se coordine el despacho.",
                                fontSize = 11.sp,
                                color = GrisEntrega
                            )

                            Text(
                                text = "Más adelante informaremos los " +
                                        "cambios de estado por correo " +
                                        "electrónico, cuando ese servicio " +
                                        "esté habilitado.",
                                fontSize = 11.sp,
                                color = GrisEntrega
                            )

                            OutlinedTextField(
                                value = direccion,
                                onValueChange = {
                                    direccion = it
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = {
                                    Text("Dirección de entrega")
                                },
                                placeholder = {
                                    Text(
                                        "Calle, número, comuna..."
                                    )
                                },
                                singleLine = true,
                                isError = !direccionValida,
                                shape = RoundedCornerShape(12.dp),
                                colors =
                                    OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor =
                                            VerdeEntrega,
                                        unfocusedBorderColor =
                                            BordeEntrega,
                                        focusedContainerColor =
                                            CremaEntrega,
                                        unfocusedContainerColor =
                                            CremaEntrega
                                    )
                            )

                            if (!direccionValida) {
                                Text(
                                    text = "Ingresa una dirección de " +
                                            "al menos 8 caracteres.",
                                    color =
                                        MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp
                                )
                            }

                        } else {
                            Text(
                                text = "El pedido será retirado en el local.",
                                color = GrisEntrega,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                //metodo de pago
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = BorderStroke(
                        1.dp,
                        BordeEntrega
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Método de pago",
                            color = CafeEntrega,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        OpcionEntregaPago(
                            titulo = "Efectivo",
                            descripcion = "Paga al retirar o recibir tu pedido.",
                            seleccionada =
                                metodoPago == "Efectivo",
                            onClick = {
                                metodoPago = "Efectivo"
                            }
                        )

                        OpcionEntregaPago(
                            titulo = "Tarjeta",
                            descripcion = "Registra tarjeta como método de pago.",
                            seleccionada =
                                metodoPago == "Tarjeta",
                            onClick = {
                                metodoPago = "Tarjeta"
                            }
                        )

                        if (metodoPago == "Tarjeta") {
                            Text(
                                text = "El pago con tarjeta todavía " +
                                        "no está integrado. Se registrará " +
                                        "el método seleccionado, pero " +
                                        "no se realizará un cobro.",
                                fontSize = 11.sp,
                                color = GrisEntrega
                            )

                            if (tipoEntrega == "Domicilio") {
                                Text(
                                    text = "Cuando se implemente el pago, " +
                                            "el monto deberá incluir los " +
                                            "productos y la tarifa de " +
                                            "despacho confirmada.",
                                    fontSize = 11.sp,
                                    color = GrisEntrega
                                )
                            }
                        } else {
                            Text(
                                text = "El pago en efectivo se coordinará " +
                                        "al retirar o recibir el pedido.",
                                fontSize = 11.sp,
                                color = GrisEntrega
                            )
                        }
                    }
                }

                //resumen del pedido
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = BeigeEntrega
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(17.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Resumen del pedido",
                            color = CafeEntrega,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Subtotal productos",
                                color = GrisEntrega,
                                fontSize = 12.sp
                            )

                            Text(
                                text = precioEntrega(total),
                                color = CafeEntrega,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Costo de despacho",
                                color = GrisEntrega,
                                fontSize = 12.sp
                            )

                            Text(
                                text = if (costoDespacho == null) {
                                    "Por confirmar"
                                } else {
                                    precioEntrega(costoDespacho)
                                },
                                color = CafeEntrega,
                                fontSize = 12.sp
                            )
                        }

                        HorizontalDivider(
                            color = Color(0xFFDCCBC1)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (costoDespacho == null) {
                                    "Total pendiente"
                                } else {
                                    "Total"
                                },
                                color = CafeEntrega,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = if (costoDespacho == null) {
                                    "Por confirmar"
                                } else {
                                    precioEntrega(
                                        total + costoDespacho
                                    )
                                },
                                color = CafeEntrega,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (costoDespacho == null) {
                            Text(
                                text = "El total final se confirmará " +
                                        "cuando se informe la tarifa de despacho.",
                                fontSize = 10.sp,
                                color = GrisEntrega
                            )
                        }
                    }
                }


                if (errorPedido.isNotEmpty()) {
                    Text(
                        text = errorPedido,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }

            //boton fijo del inferior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CremaEntrega)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 12.dp
                    )
            ) {
                Button(
                    onClick = {
                        onConfirmarPedido(
                            tipoEntrega,
                            metodoPago,
                            if (tipoEntrega == "Domicilio") {
                                direccion.trim()
                            } else {
                                ""
                            }
                        )
                    },
                    enabled = puedeConfirmar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CafeEntrega,
                        contentColor = Color.White
                    )
                ) {
                    if (guardandoPedido) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )

                        Spacer(Modifier.width(10.dp))

                        Text("Guardando...")
                    } else {
                        Text(
                            text = "Confirmar pedido  ›",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                TextButton(
                    onClick = onVolverCarrito,
                    enabled = !guardandoPedido,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Volver al carrito",
                        color = CafeEntrega,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

//para seleciionar pago
@Composable
private fun OpcionEntregaPago(
    titulo: String,
    descripcion: String,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (seleccionada) {
            Color(0xFFF0F6F0)
        } else {
            CremaEntrega
        },
        border = BorderStroke(
            if (seleccionada) 1.5.dp else 1.dp,
            if (seleccionada) VerdeEntrega else BordeEntrega
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 10.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = seleccionada,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = VerdeEntrega
                )
            )

            Spacer(Modifier.width(6.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = titulo,
                    color = CafeEntrega,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(3.dp))

                Text(
                    text = descripcion,
                    color = GrisEntrega,
                    fontSize = 11.sp
                )
            }
        }
    }
}
