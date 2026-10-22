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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.cocoa_market_spa.catalogo.viewmodel.CatalogoViewModel
import com.example.cocoa_market_spa.data.local.AppDatabase
import androidx.compose.material3.Text

@Composable
fun CatalogoNavigation() {

    val navController = rememberNavController()

    val context = LocalContext.current

    val database = remember {
        AppDatabase.getDatabase(context)
    }

    val catalogoViewModel = remember {
        CatalogoViewModel(database.ProductoDao())
    }

    LaunchedEffect(Unit) {
        catalogoViewModel.cargarProductos()
    }

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
                        productos = catalogoViewModel.productos.value,
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

            LaunchedEffect(id) {
                if (id != null) {
                    catalogoViewModel.cargarProductoPorId(id)
                }
            }

            val producto = catalogoViewModel.productoSeleccionado.value

            if (producto != null && producto.id == id) {

                DetalleProductoScreen(
                    nombre = producto.nombre,
                    precio = producto.precio,
                    descripcion = producto.descripcion,
                    stock = producto.stock,
                    imagen = producto.imagen,
                    onVolver = {
                        navController.popBackStack()
                    }
                )

            } else {
                Text("Cargando producto...")
            }
        }
    }
}
