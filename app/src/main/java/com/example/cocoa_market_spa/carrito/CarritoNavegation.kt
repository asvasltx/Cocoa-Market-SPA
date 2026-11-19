
package com.example.cocoa_market_spa.carrito

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cocoa_market_spa.data.local.AppDatabase
import com.example.cocoa_market_spa.viewmodel.CarritoViewModel
import com.example.cocoa_market_spa.viewmodel.PedidoViewModel

// Navegacion alternativa del carrito.
// La navegacion principal es CatalogoNavigation.
@Composable
fun CarritoNavigation(
    usuarioEmail: String
) {

    val navController = rememberNavController()
    val context = LocalContext.current

    // Acceso a la base de datos local
    val database = remember {
        AppDatabase.getDatabase(context)
    }

    val carritoViewModel: CarritoViewModel = viewModel()

    // Ahora identifica al comprador mediante su correo
    val pedidoViewModel: PedidoViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {

                @Suppress("UNCHECKED_CAST")
                return PedidoViewModel(
                    database,
                    usuarioEmail
                ) as T
            }
        }
    )

    // Informacion de la compra confirmada
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

    // Guarda el costo del despacho para la confirmacion
    var costoDespachoPedido by remember {
        mutableStateOf<Int?>(0)
    }

    NavHost(
        navController = navController,
        startDestination = "carrito"
    ) {

        // CARRITO
        composable("carrito") {

            CarritoScreen(
                onNavigateToPedidos = {
                    navController.navigate("entrega_pago")
                },

                onVolverCatalogo = {
                    navController.popBackStack()
                },

                carritoViewModel = carritoViewModel
            )
        }

        // ENTREGA Y PAGO
        composable("entrega_pago") {

            EntregaPagoScreen(
                total = carritoViewModel.calcularTotal(),
                guardandoPedido = pedidoViewModel.guardandoPedido.value,
                errorPedido = pedidoViewModel.errorPedido.value,

                onConfirmarPedido = { entrega, pago, direccion ->

                    val total = carritoViewModel.calcularTotal()

                    pedidoViewModel.agregarPedido(
                        total = total,
                        productos = carritoViewModel.productos.value,
                        tipoEntrega = entrega,
                        metodoPago = pago,
                        direccion = direccion,

                        onGuardado = {

                            // Guarda los datos de la compra
                            tipoEntregaPedido = entrega
                            metodoPagoPedido = pago
                            direccionPedido = direccion

                            // Consulta el costo de despacho configurado
                            costoDespachoPedido =
                                if (entrega == "Domicilio") {
                                    ConfiguracionEntrega.COSTO_DESPACHO
                                } else {
                                    0
                                }

                            // Suma el despacho solo si tiene precio
                            totalPedido =
                                total + (costoDespachoPedido ?: 0)

                            // Vacia el carrito solamente al guardar
                            carritoViewModel.vaciarCarrito()

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

        // CONFIRMACION DEL PEDIDO
        composable("pedido_confirmado") {

            PedidoConfirmadoScreen(
                total = totalPedido,
                tipoEntrega = tipoEntregaPedido,
                metodoPago = metodoPagoPedido,
                direccion = direccionPedido,

                // Parametro que faltaba anteriormente
                costoDespacho = costoDespachoPedido,

                onVerPedidos = {
                    navController.navigate("pedidos")
                },

                onVolverCatalogo = {
                    navController.navigate("carrito")
                }
            )
        }

        // HISTORIAL DE PEDIDOS
        composable("pedidos") {

            PedidoScreen(
                onVolvercarrito = {
                    navController.navigate("carrito")
                },

                pedidoViewModel = pedidoViewModel
            )
        }
    }
}
