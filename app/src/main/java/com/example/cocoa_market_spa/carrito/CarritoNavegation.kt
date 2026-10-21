package com.example.cocoa_market_spa.carrito


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun CarritoNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "carrito"
    ) {

        composable("carrito") {
            CarritoScreen(onNavigateToPedidos = {
                navController.navigate("pedidos")
            })
        }

        composable("pedidos") {
            PedidoScreen(
                onVolvercarrito = {
                    navController.popBackStack()
                }
            )
        }
    }
}
