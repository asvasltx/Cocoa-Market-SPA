
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
    onCerrarSesion: () -> Unit
) {

    // Controla las pantallas del comprador
    val navController = rememberNavController()

    val context = LocalContext.current

    // ViewModel del carrito
    val carritoViewModel: CarritoViewModel = viewModel()

    // Base de datos comercial temporal
    val database = remember {
        AppDatabase.getDatabase(context)
    }

    // ViewModel compartido para guardar y consultar pedidos
    val pedidoViewModel: PedidoViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {

                @Suppress("UNCHECKED_CAST")
                return PedidoViewModel(database) as T
            }
        }
    )

    // ViewModel del catalogo
    val catalogoViewModel = remember {
        CatalogoViewModel(database.ProductoDao())
    }

    // Datos de la ultima compra confirmada
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

    NavHost(
        navController = navController,
        startDestination = "catalogo"
    ) {

        // CATALOGO PRINCIPAL
        composable("catalogo") {

            // Actualiza los productos al entrar al catalogo
            LaunchedEffect(Unit) {
                catalogoViewModel.cargarProductos()
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),

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

        // CARRITO DE COMPRAS
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

        // ENTREGA Y PAGO
        composable("entrega_pago") {

            EntregaPagoScreen(
                total = carritoViewModel.calcularTotal(),
                guardandoPedido = pedidoViewModel.guardandoPedido.value,
                errorPedido = pedidoViewModel.errorPedido.value,

                onConfirmarPedido = { entrega, pago, direccion ->

                    val total = carritoViewModel.calcularTotal()

                    // Registra el pedido antes de mostrar confirmacion
                    pedidoViewModel.agregarPedido(
                        total = total,
                        productos = carritoViewModel.productos.value,
                        tipoEntrega = entrega,
                        metodoPago = pago,
                        direccion = direccion,

                        onGuardado = {

                            // Conserva los datos para el resumen
                            totalPedido = total
                            tipoEntregaPedido = entrega
                            metodoPagoPedido = pago
                            direccionPedido = direccion

                            // Vacia el carrito solo si el guardado funciono
                            carritoViewModel.vaciarCarrito()

                            // Actualiza el inventario visible
                            catalogoViewModel.cargarProductos()

                            // Reproduce el sonido de confirmacion
                            try {

                                val sonido = RingtoneManager.getRingtone(
                                    context,
                                    RingtoneManager.getDefaultUri(
                                        RingtoneManager.TYPE_NOTIFICATION
                                    )
                                )

                                sonido?.play()

                            } catch (e: Exception) {

                                // El pedido sigue guardado si falla el sonido
                            }

                            // Abre la confirmacion
                            navController.navigate("pedido_confirmado") {

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

        // CONFIRMACION DE COMPRA
        composable("pedido_confirmado") {

            PedidoConfirmadoScreen(
                total = totalPedido,
                tipoEntrega = tipoEntregaPedido,
                metodoPago = metodoPagoPedido,
                direccion = direccionPedido,

                onVerPedidos = {
                    navController.navigate("pedidos")
                },

                onVolverCatalogo = {
                    navController.navigate("catalogo")
                }
            )
        }

        // HISTORIAL DE PEDIDOS
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
            ) { innerPadding ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {

                    MiCuentaScreen(
                        nombreUsuario = nombreUsuario,

                        // Acceso directo al historial de compras
                        onVerPedidos = {
                            navController.navigate("pedidos")
                        },

                        // Regresa al catalogo
                        onVolverCatalogo = {
                            navController.navigate("catalogo")
                        },

                        // NUEVO: cierra la sesion del comprador
                        onCerrarSesion = {

                            // Limpia el carrito del usuario anterior
                            carritoViewModel.vaciarCarrito()

                            // Solicita volver al login principal
                            onCerrarSesion()
                        }
                    )
                }
            }
        }

        // DETALLE DE PRODUCTO
        composable("detalle/{id}") { backStackEntry ->

            // Obtiene el ID del producto seleccionado
            val id = backStackEntry.arguments
                ?.getString("id")
                ?.toIntOrNull()

            // Consulta el detalle actualizado
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

                        // Convierte el producto a un item del carrito
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
