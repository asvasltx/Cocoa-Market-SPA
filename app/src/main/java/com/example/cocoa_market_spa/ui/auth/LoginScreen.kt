
package com.example.cocoa_market_spa.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cocoa_market_spa.R

// COLORES COCOA MARKET
private val Cafe = Color(0xFF603B2D)
private val Crema = Color(0xFFFCF8F3)
private val Verde = Color(0xFF64865B)
private val Borde = Color(0xFFE0D6CE)

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
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


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(185.dp)
                .clip(
                    RoundedCornerShape(
                        bottomStart = 28.dp,
                        bottomEnd = 28.dp
                    )
                )
                .background(Cafe)
        ) {


            Image(
                painter = painterResource(
                    id = R.drawable.cocoa_market_logo
                ),
                contentDescription = "Logo Cocoa Market",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 22.dp, top = 22.dp)
                    .size(64.dp)
                    .clip(RoundedCornerShape(50.dp)),
                contentScale = ContentScale.Fit
            )


            Text(
                text = "Cocoa\nMarket",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 19.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 94.dp, top = 34.dp)
            )


            Text(
                text = "🍃",
                fontSize = 53.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 65.dp, bottom = 24.dp)
            )

            Text(
                text = "🍫",
                fontSize = 42.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 14.dp)
            )
        }


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
                text = "¡Qué bueno verte!",
                color = Cafe,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Ingresa para hacer tu pedido fácil y rápido.",
                color = Color(0xFF8B7770),
                fontSize = 13.sp
            )

            Spacer(Modifier.height(22.dp))

            //correo
            Text(
                text = "Correo electrónico",
                color = Cafe,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(7.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.clearError()
                },
                placeholder = {
                    Text(
                        text = "nombre@correo.cl",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
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

            Spacer(Modifier.height(17.dp))

            //contrasela
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
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cafe,
                    unfocusedBorderColor = Borde,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            //recuperacion
            Text(
                text = "¿Olvidaste tu contraseña?",
                color = Cafe,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 10.dp)
            )


            if (error != null) {
                Spacer(Modifier.height(10.dp))

                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(22.dp))

            //boron de ingresar
            Button(
                onClick = {
                    viewModel.login(
                        email = email,
                        pass = password,
                        onSuccess = onLoginSuccess
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
                    text = "Ingresar  ›",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(13.dp))

            //ir al registro
            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿Aún no tienes cuenta? ",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Text(
                    text = "Crear cuenta",
                    color = Cafe,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
