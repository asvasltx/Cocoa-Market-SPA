
package com.example.cocoa_market_spa.catalogo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// COLORES DE COCOA MARKET
private val CafePerfil = Color(0xFF603B2D)
private val CremaPerfil = Color(0xFFFCF8F3)
private val BeigePerfil = Color(0xFFF3E8E1)
private val GrisPerfil = Color(0xFF807A75)

@Composable
fun MiCuentaScreen(
    nombreUsuario: String,
    onVerPedidos: () -> Unit,
    onVolverCatalogo: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CremaPerfil
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
                    .height(62.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Mi perfil",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CafePerfil
                )
            }

            HorizontalDivider(
                color = Color(0xFFE9E2DC),
                thickness = 1.dp
            )

            //contenido del centro
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    //icono de perfil
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(
                                BeigePerfil,
                                RoundedCornerShape(22.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PersonOutline,
                            contentDescription = "Perfil de usuario",
                            tint = CafePerfil,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))


                    Text(
                        text = "Hola, $nombreUsuario",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CafePerfil,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Administra tus datos, direcciones y preferencias.",
                        fontSize = 12.sp,
                        color = GrisPerfil,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(Modifier.height(24.dp))

                    //boton princimal
                    Button(
                        onClick = onVolverCatalogo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CafePerfil,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Text(
                            text = "Explorar catálogo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    //acceso a pedidos
                    OutlinedButton(
                        onClick = onVerPedidos,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CafePerfil
                        )
                    ) {
                        Text(
                            text = "Mis pedidos",
                            fontSize = 13.sp
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    //cierre de sesion
                    TextButton(
                        onClick = onCerrarSesion
                    ) {
                        Text(
                            text = "Cerrar sesión",
                            color = CafePerfil,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
