
package com.example.cocoa_market_spa.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cocoa_market_spa.R

//colores de cocoa
private val Cafe = Color(0xFF603B2D)
private val Crema = Color(0xFFFCF8F3)
private val Verde = Color(0xFF64865B)
private val Borde = Color(0xFFE0D6CE)

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegistered: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }

    val error by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Crema)
            .verticalScroll(rememberScrollState())
    ) {

        // encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(205.dp)
                .clip(
                    RoundedCornerShape(
                        bottomStart = 28.dp,
                        bottomEnd = 28.dp
                    )
                )
                .background(Cafe),
            contentAlignment = Alignment.Center
        ) {

            // LOGO COCOA MARKET
            Image(
                painter = painterResource(
                    id = R.drawable.cocoa_market_logo
                ),
                contentDescription = "Logo Cocoa Market",
                modifier = Modifier
                    .size(155.dp)
                    .clip(RoundedCornerShape(100.dp)),
                contentScale = ContentScale.Fit
            )
        }

        // contenido de logo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 25.dp,
                    vertical = 22.dp
                )
        ) {

            Text(
                text = "TU ALMACÉN DE BARRIO",
                color = Verde,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(Modifier.height(7.dp))

            Text(
                text = "¡Únete a Cocoa Market!",
                color = Cafe,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Crea tu cuenta y disfruta de tus compras.",
                color = Color(0xFF8B7770),
                fontSize = 13.sp
            )

            Spacer(Modifier.height(22.dp))


            CampoRegistro(
                titulo = "Nombre completo",
                valor = name,
                placeholder = "Tu nombre completo",
                onValueChange = {
                    name = it
                    viewModel.clearError()
                }
            )

            Spacer(Modifier.height(14.dp))


            CampoRegistro(
                titulo = "Correo electrónico",
                valor = email,
                placeholder = "nombre@correo.cl",
                tipo = KeyboardType.Email,
                onValueChange = {
                    email = it
                    viewModel.clearError()
                }
            )

            Spacer(Modifier.height(14.dp))


            Text(
                text = "Contraseña",
                color = Cafe,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(7.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.clearError()
                },
                placeholder = {
                    Text(
                        text = "Tu contraseña",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                },
                visualTransformation =
                    if (mostrarPassword)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                trailingIcon = {
                    TextButton(
                        onClick = {
                            mostrarPassword = !mostrarPassword
                        }
                    ) {
                        Text(
                            text = if (mostrarPassword)
                                "Ocultar"
                            else
                                "Ver",
                            color = Cafe,
                            fontSize = 12.sp
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cafe,
                    unfocusedBorderColor = Borde,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )


            if (error != null) {

                Spacer(Modifier.height(10.dp))

                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(24.dp))


            Button(
                onClick = {
                    viewModel.register(
                        name = name,
                        email = email,
                        pass = password,
                        onSuccess = onRegistered
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Cafe,
                    contentColor = Color.White
                )
            ) {

                Text(
                    text = "Crear cuenta  ›",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(13.dp))

            // VOLVER AL LOGIN
            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "¿Ya tienes cuenta? ",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Text(
                    text = "Inicia sesión",
                    color = Cafe,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}


@Composable
private fun CampoRegistro(
    titulo: String,
    valor: String,
    placeholder: String,
    tipo: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {

    Text(
        text = titulo,
        color = Cafe,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
    )

    Spacer(Modifier.height(7.dp))

    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 12.sp,
                color = Color.Gray
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = tipo
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Cafe,
            unfocusedBorderColor = Borde,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}
