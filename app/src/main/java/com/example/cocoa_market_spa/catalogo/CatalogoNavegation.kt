package com.example.cocoa_market_spa.catalogo

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cocoa_market_spa.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier

@Composable
fun CatalogoNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "catalogo"
    ) {
        //composable para la barra inferior

        composable("catalogo") {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    BarraInferior()
                }
            ) { innerPadding ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {

                    CatalogoScreen(
                        onProductoClick = { id ->
                            navController.navigate("detalle/$id")
                        }
                    )
                }
            }
        }

        //Para ver el detalle de los productos
        composable("detalle/{id}") { backStackEntry ->

            val id = backStackEntry.arguments
                ?.getString("id")
                ?.toIntOrNull()

            when (id) {

                1 -> DetalleProductoScreen(
                    nombre = "Arroz Tucapel 1 kg",
                    precio = 1290,
                    descripcion = "Arroz de 1 kg para preparar diferentes comidas.",
                    stock = 15,
                    imagen = R.drawable.arroztucapel,
                    onVolver = { navController.popBackStack() }
                )

                2 -> DetalleProductoScreen(
                    nombre = "Leche Chocolate Colun 200 ml",
                    precio = 1490,
                    descripcion = "Leche con sabor a chocolate en envase de 200 ml.",
                    stock = 20,
                    imagen = R.drawable.lechechocolate,
                    onVolver = { navController.popBackStack() }
                )

                3 -> DetalleProductoScreen(
                    nombre = "Aceite Maravilla 1 L",
                    precio = 2000,
                    descripcion = "Aceite de maravilla de 1 litro para cocinar.",
                    stock = 12,
                    imagen = R.drawable.aceite,
                    onVolver = { navController.popBackStack() }
                )

                4 -> DetalleProductoScreen(
                    nombre = "Pasta de dientes Pepsodent",
                    precio = 1000,
                    descripcion = "Pasta dental para la higiene bucal diaria.",
                    stock = 18,
                    imagen = R.drawable.pastadedientes,
                    onVolver = { navController.popBackStack() }
                )

                else -> {
                    navController.popBackStack()
                }
            }
        }
    }
}
