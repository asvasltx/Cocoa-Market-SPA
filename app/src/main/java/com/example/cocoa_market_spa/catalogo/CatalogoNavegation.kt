
package com.example.cocoa_market_spa.catalogo

import android.media.RingtoneManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cocoa_market_spa.carrito.CarritoScreen
import com.example.cocoa_market_spa.carrito.ConfiguracionEntrega
import com.example.cocoa_market_spa.carrito.EntregaPagoScreen
import com.example.cocoa_market_spa.carrito.PedidoConfirmadoScreen
import com.example.cocoa_market_spa.carrito.PedidoScreen
import com.example.cocoa_market_spa.catalogo.viewmodel.CatalogoViewModel
import com.example.cocoa_market_spa.data.local.AppDatabase
import com.example.cocoa_market_spa.model.CarritoModel
import com.example.cocoa_market_spa.viewmodel.CarritoViewModel
import com.example.cocoa_market_spa.viewmodel.PedidoViewModel

@Composable
fun CatalogoNavigation(
    nombreUsuario: String,
    correoUsuario: String,
    onCerrarSesion: () -> Unit
) {

    val navController = rememberNavController()
    val context = LocalContext.current

    // ViewModel compartido del carrito.
    val carritoViewModel: CarritoViewModel = viewModel()

    // Base local utilizada temporalmente para los productos.
    val database = remember {
        AppDatabase.getDatabase(context)
    }

    // Cada comprador consulta sus propios pedidos.
    val pedidoViewModel: PedidoViewModel = viewModel(
        key = "pedidos_$correoUsuario",
        factory = object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {

                @Suppress("UNCHECKED_CAST")
                return PedidoViewModel(
                    database = database,
                    usuarioEmail = correoUsuario
                ) as T
            }
        }
    )

    val catalogoViewModel = remember {
        CatalogoViewModel(database.ProductoDao())
    }

    // Datos del ultimo pedido confirmado.
    var totalPedido by remember {
        mutableStateOf(0)
    }

    var tipoEntregaPedido by remember {
        mutableStateOf("")
    }

    var metodoPagoPedido by remember {
        mutableStateOf("")
    }

    var direccionPedido by remember {
        mutableStateOf("")
    }

    var costoDespachoPedido by remember {
        mutableStateOf<Int?>(0)
    }

    NavHost(
        navController = navController,
        startDestination = "catalogo"
    ) {

        // CATALOGO
        composable("catalogo") {

            LaunchedEffect(Unit) {
                catalogoViewModel.cargarProductos()
            }

            Scaffold(
                bottomBar = {
                    BarraInferior(
                        onInicio = {
                            navController.navigate("catalogo") {
                                launchSingleTop = true
                            }
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
            ) { paddingValues ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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

        // CARRITO
        composable("carrito") {

            Scaffold(
                bottomBar = {
                    BarraInferior(
                        onInicio = {
                            navController.navigate("catalogo")
                        },
                        onCarrito = {
                            navController.navigate("carrito") {
                                launchSingleTop = true
                            }
                        },
                        onPedidos = {
                            navController.navigate("pedidos")
                        },
                        onCuenta = {
                            navController.navigate("cuenta")
                        }
                    )
                }
            ) { paddingValues ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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

        // ENTREGA Y PAGO
        composable("entrega_pago") {

            EntregaPagoScreen(
                total = carritoViewModel.calcularTotal(),
                guardandoPedido =
                    pedidoViewModel.guardandoPedido.value,
                errorPedido =
                    pedidoViewModel.errorPedido.value,

                onConfirmarPedido = { entrega, pago, direccion ->

                    val subtotal = carritoViewModel.calcularTotal()

                    // Guarda primero la compra.
                    pedidoViewModel.agregarPedido(
                        total = subtotal,
                        productos = carritoViewModel.productos.value,
                        tipoEntrega = entrega,
                        metodoPago = pago,
                        direccion = direccion,

                        onGuardado = {

                            costoDespachoPedido =
                                if (entrega == "Domicilio") {
                                    ConfiguracionEntrega.COSTO_DESPACHO
                                } else {
                                    0
                                }

                            // Conserva el resumen de la compra.
                            totalPedido =
                                subtotal + (costoDespachoPedido ?: 0)

                            tipoEntregaPedido = entrega
                            metodoPagoPedido = pago
                            direccionPedido = direccion

                            // Solo vacia despues del guardado correcto.
                            carritoViewModel.vaciarCarrito()

                            // Refresca el catalogo y el stock.
                            catalogoViewModel.cargarProductos()

                            // Mantiene el sonido de confirmacion.
                            try {

                                val sonido =
                                    RingtoneManager.getRingtone(
                                        context,
                                        RingtoneManager.getDefaultUri(
                                            RingtoneManager.TYPE_NOTIFICATION
                                        )
                                    )

                                sonido?.play()

                            } catch (e: Exception) {
                                // El pedido ya quedo guardado.
                            }

                            navController.navigate(
                                "pedido_confirmado"
                            ) {
                                popUpTo("entrega_pago") {
                                    inclusive = true
                                }
                            }
                        }
                    )
                },

                onVolverCarrito = {
                    navController.popBackStack()
                }
            )
        }

        // PEDIDO CONFIRMADO
        composable("pedido_confirmado") {

            PedidoConfirmadoScreen(
                total = totalPedido,
                tipoEntrega = tipoEntregaPedido,
                metodoPago = metodoPagoPedido,
                direccion = direccionPedido,
                costoDespacho = costoDespachoPedido,

                onVerPedidos = {
                    navController.navigate("pedidos")
                },

                onVolverCatalogo = {
                    navController.navigate("catalogo")
                }
            )
        }

        // MIS PEDIDOS
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
                            navController.navigate("pedidos") {
                                launchSingleTop = true
                            }
                        },
                        onCuenta = {
                            navController.navigate("cuenta")
                        }
                    )
                }
            ) { paddingValues ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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

        // MI CUENTA
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
                            navController.navigate("cuenta") {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            ) { paddingValues ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {

                    MiCuentaScreen(
                        nombreUsuario = nombreUsuario,

                        onVerPedidos = {
                            navController.navigate("pedidos")
                        },

                        onVolverCatalogo = {
                            navController.navigate("catalogo")
                        },

                        onCerrarSesion = {

                            // No conserva el carrito del usuario anterior.
                            carritoViewModel.vaciarCarrito()
                            onCerrarSesion()
                        }
                    )
                }
            }
        }

        // DETALLE DE PRODUCTO
        composable("detalle/{id}") { backStackEntry ->

            val id = backStackEntry.arguments
                ?.getString("id")
                ?.toIntOrNull()

            LaunchedEffect(id) {
                if (id != null) {
                    catalogoViewModel.cargarProductoPorId(id)
                }
            }

            val producto =
                catalogoViewModel.productoSeleccionado.value

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

                        val productoCarrito = CarritoModel(
                            productoId = producto.id,
                            nombre = producto.nombre,
                            precio = producto.precio,
                            cantidad = cantidad,
                            imagen = producto.imagen
                        )

                        carritoViewModel.agregarProducto(
                            productoCarrito
                        )
                    }
                )

            } else {

                Text("Cargando producto...")
            }
        }
    }
}
