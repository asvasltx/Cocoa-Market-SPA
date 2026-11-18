
package com.example.cocoa_market_spa.catalogo

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cocoa_market_spa.viewmodel.CarritoViewModel
import com.example.cocoa_market_spa.model.CarritoModel
import com.example.cocoa_market_spa.carrito.CarritoScreen
import com.example.cocoa_market_spa.carrito.EntregaPagoScreen
import com.example.cocoa_market_spa.carrito.PedidoConfirmadoScreen
import com.example.cocoa_market_spa.viewmodel.PedidoViewModel
import com.example.cocoa_market_spa.carrito.PedidoScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.media.RingtoneManager

@Composable
fun CatalogoNavigation(nombreUsuario: String) {

    // Controla la navegacion entre las pantallas del comprador
    val navController = rememberNavController()

    // Permite acceder a recursos del telefono
    val context = LocalContext.current

    // ViewModel del carrito de compras
    val carritoViewModel: CarritoViewModel = viewModel()

    // Acceso a los pedidos actuales en SQLite
    val pedidoDao = AppDatabase.getDatabase(context).PedidoDao()

    val pedidoViewModel: PedidoViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PedidoViewModel(pedidoDao) as T
            }
        }
    )

    // Guarda el total del pedido confirmado
    var totalPedido by remember {
        mutableStateOf(0)
    }

    // Base de datos actual del proyecto
    val database = remember {
        AppDatabase.getDatabase(context)
    }

    // ViewModel del catalogo
    val catalogoViewModel = remember {
        CatalogoViewModel(database.ProductoDao())
    }

    // Carga los productos al iniciar el catalogo
    LaunchedEffect(Unit) {
        catalogoViewModel.cargarProductos()
    }

    NavHost(
        navController = navController,
        startDestination = "catalogo"
    ) {

        // PANTALLA PRINCIPAL DEL CATALOGO
        composable("catalogo") {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    BarraInferior(
                        onInicio = {
                            navController.navigate("catalogo")
                        },
                        onCarrito = {
                            navController.navigate("carrito")
                        },
                        onPedidos = {
                            navController.navigate("pedidos")
                        },
                        onCuenta = {
                            // CORREGIDO: la ruta es cuenta en minuscula
                            navController.navigate("cuenta")
                        }
                    )
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

        // PANTALLA DEL CARRITO
        composable("carrito") {

            Scaffold(
                bottomBar = {
                    BarraInferior(
                        onInicio = {
                            navController.navigate("catalogo")
                        },
                        onCarrito = {
                            navController.navigate("carrito")
                        },
                        onPedidos = {
                            navController.navigate("pedidos")
                        },
                        onCuenta = {
                            navController.navigate("cuenta")
                        }
                    )
                }
            ) { innerPadding ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {

                    CarritoScreen(
                        onNavigateToPedidos = {
                            navController.navigate("entrega_pago")
                        },
                        onVolverCatalogo = {
                            navController.navigate("catalogo")
                        },
                        carritoViewModel = carritoViewModel
                    )
                }
            }
        }

        // PANTALLA DE ENTREGA Y PAGO
        composable("entrega_pago") {

            EntregaPagoScreen(
                onConfirmarPedido = {

                    totalPedido = carritoViewModel.calcularTotal()

                    // Comprueba que existan productos en el carrito
                    if (carritoViewModel.productos.value.isNotEmpty()) {

                        // Guarda el pedido mediante su ViewModel actual
                        pedidoViewModel.agregarPedido(
                            total = totalPedido,
                            productos = carritoViewModel.productos.value
                        )

                        // Limpia el carrito despues de confirmar
                        carritoViewModel.vaciarCarrito()

                        // Reproduce un sonido de confirmacion
                        val sonido = RingtoneManager.getRingtone(
                            context,
                            RingtoneManager.getDefaultUri(
                                RingtoneManager.TYPE_NOTIFICATION
                            )
                        )

                        sonido?.play()

                        // Abre la confirmacion del pedido
                        navController.navigate("pedido_confirmado")
                    }
                },
                onVolverCarrito = {
                    navController.popBackStack()
                }
            )
        }

        // PANTALLA DE CONFIRMACION DEL PEDIDO
        composable("pedido_confirmado") {

            PedidoConfirmadoScreen(
                total = totalPedido,
                onVerPedidos = {
                    navController.navigate("pedidos")
                },
                onVolverCatalogo = {
                    // CORREGIDO: vuelve al catalogo y no al carrito
                    navController.navigate("catalogo")
                }
            )
        }

        // PANTALLA DEL HISTORIAL DE PEDIDOS
        composable("pedidos") {

            Scaffold(
                bottomBar = {
                    BarraInferior(
                        onInicio = {
                            navController.navigate("catalogo")
                        },
                        onCarrito = {
                            navController.navigate("carrito")
                        },
                        onPedidos = {
                            navController.navigate("pedidos")
                        },
                        onCuenta = {
                            navController.navigate("cuenta")
                        }
                    )
                }
            ) { innerPadding ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {

                    PedidoScreen(
                        onVolvercarrito = {
                            navController.navigate("carrito")
                        },
                        pedidoViewModel = pedidoViewModel
                    )
                }
            }
        }

        // PANTALLA MI CUENTA
        composable("cuenta") {

            Scaffold(
                bottomBar = {
                    BarraInferior(
                        onInicio = {
                            navController.navigate("catalogo")
                        },
                        onCarrito = {
                            navController.navigate("carrito")
                        },
                        onPedidos = {
                            navController.navigate("pedidos")
                        },
                        onCuenta = {
                            navController.navigate("cuenta")
                        }
                    )
                }
            ) { innerPadding ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {

                    MiCuentaScreen(
                        nombreUsuario = nombreUsuario
                    )
                }
            }
        }

        // PANTALLA DE DETALLE DE PRODUCTO
        composable("detalle/{id}") { backStackEntry ->

            // Obtiene el ID del producto seleccionado
            val id = backStackEntry.arguments
                ?.getString("id")
                ?.toIntOrNull()

            // Busca el producto en la base de datos
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
                    },
                    onAgregarCarrito = { cantidad ->

                        // Crea el producto para agregarlo al carrito
                        val productoCarrito = CarritoModel(
                            productoId = producto.id,
                            nombre = producto.nombre,
                            precio = producto.precio,
                            cantidad = cantidad,
                            imagen = producto.imagen
                        )

                        carritoViewModel.agregarProducto(productoCarrito)
                    }
                )

            } else {
                Text("Cargando producto...")
            }
        }
    }
}
