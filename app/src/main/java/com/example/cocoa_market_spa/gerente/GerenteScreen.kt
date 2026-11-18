
package com.example.cocoa_market_spa.gerente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cocoa_market_spa.data.local.entity.ProductoEntity
import com.example.cocoa_market_spa.ui.theme.CafePrincipal
import com.example.cocoa_market_spa.ui.theme.CremaFondo

@Composable
fun GerenteScreen(
    nombreGerente: String,
    onCerrarSesion: () -> Unit
) {

    // ViewModel encargado de productos y pedidos
    val gerenteViewModel: GerenteViewModel = viewModel()

    // Pantalla que esta utilizando el gerente
    var seccionActual by remember {
        mutableStateOf("INICIO")
    }

    // Producto que se esta editando
    var productoEditando by remember {
        mutableStateOf<ProductoEntity?>(null)
    }

    // Carga los productos y pedidos al abrir el panel
    LaunchedEffect(Unit) {
        gerenteViewModel.cargarDatos()
    }

    val productos = gerenteViewModel.productos.value
    val pedidos = gerenteViewModel.pedidos.value
    val ocupado = gerenteViewModel.operacionEnCurso.value

    // Indicadores reales del inventario local
    val bajoStock = productos.count {
        it.stock <= 5
    }

    val pendientes = pedidos.count {
        it.estado != "Entregado" &&
                it.estado != "Cancelado"
    }

    val montoPedidos = pedidos.sumOf {
        it.total.toLong()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CremaFondo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Cabecera principal
        Text(
            text = "Cocoa Market",
            style = MaterialTheme.typography.headlineMedium,
            color = CafePrincipal
        )

        Text(
            text = "Administracion del minimarket",
            color = CafePrincipal
        )

        // Permite regresar al menu principal
        if (seccionActual != "INICIO") {

            OutlinedButton(
                onClick = {
                    seccionActual = "INICIO"
                    gerenteViewModel.cargarDatos()
                }
            ) {
                Text("Volver al panel")
            }
        }

        // Mensajes de confirmacion o error
        if (gerenteViewModel.mensaje.value.isNotEmpty()) {

            Text(
                text = gerenteViewModel.mensaje.value,
                color = CafePrincipal
            )
        }

        when (seccionActual) {

            // MENU PRINCIPAL
            "INICIO" -> {

                Text(
                    text = "Panel del Gerente",
                    style = MaterialTheme.typography.titleLarge
                )

                Text("Bienvenido, $nombreGerente")

                Text("Productos registrados: ${productos.size}")
                Text("Pedidos pendientes: $pendientes")
                Text("Productos con stock bajo: $bajoStock")

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        seccionActual = "PRODUCTOS"
                        gerenteViewModel.cargarDatos()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CafePrincipal
                    )
                ) {
                    Text("Productos e inventario")
                }

                Button(
                    onClick = {
                        seccionActual = "PEDIDOS"
                        gerenteViewModel.cargarDatos()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CafePrincipal
                    )
                ) {
                    Text("Gestion de pedidos")
                }

                Button(
                    onClick = {
                        seccionActual = "REPORTES"
                        gerenteViewModel.cargarDatos()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CafePrincipal
                    )
                ) {
                    Text("Reportes y estadisticas")
                }

                OutlinedButton(
                    onClick = onCerrarSesion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesion")
                }
            }

            // PRODUCTOS E INVENTARIO
            "PRODUCTOS" -> {

                Text(
                    text = "Productos e inventario",
                    style = MaterialTheme.typography.titleLarge
                )

                // Abre el formulario para crear productos
                Button(
                    onClick = {
                        productoEditando = null
                        gerenteViewModel.limpiarMensaje()
                        seccionActual = "FORMULARIO"
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar producto")
                }

                if (productos.isEmpty()) {
                    Text("No hay productos registrados.")
                }

                // Muestra los productos existentes
                for (producto in productos) {

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = producto.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text("Categoria: ${producto.categoria}")
                            Text("Precio: $${producto.precio}")
                            Text("Stock: ${producto.stock}")

                            // Control rapido del stock
                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {

                                Button(
                                    onClick = {
                                        gerenteViewModel.modificarStock(
                                            producto,
                                            -1
                                        )
                                    },
                                    enabled = producto.stock > 0 && !ocupado
                                ) {
                                    Text("-1")
                                }

                                Button(
                                    onClick = {
                                        gerenteViewModel.modificarStock(
                                            producto,
                                            1
                                        )
                                    },
                                    enabled = !ocupado
                                ) {
                                    Text("+1")
                                }
                            }

                            // Abre el formulario con los datos actuales
                            Button(
                                onClick = {
                                    productoEditando = producto
                                    gerenteViewModel.limpiarMensaje()
                                    seccionActual = "FORMULARIO"
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Editar producto")
                            }
                        }
                    }
                }
            }

            // FORMULARIO PARA CREAR O EDITAR PRODUCTOS
            "FORMULARIO" -> {

                FormularioProductoGerente(
                    producto = productoEditando,
                    guardando = ocupado,

                    onGuardar = {
                            nombre, precio, categoria, descripcion, stock ->

                        gerenteViewModel.guardarProducto(
                            productoOriginal = productoEditando,
                            nombre = nombre,
                            precioTexto = precio,
                            categoria = categoria,
                            descripcion = descripcion,
                            stockTexto = stock,

                            onGuardado = {
                                seccionActual = "PRODUCTOS"
                            }
                        )
                    },

                    onCancelar = {
                        seccionActual = "PRODUCTOS"
                    }
                )
            }

            // GESTION DE PEDIDOS
            "PEDIDOS" -> {

                Text(
                    text = "Gestion de pedidos",
                    style = MaterialTheme.typography.titleLarge
                )

                Button(
                    onClick = {
                        gerenteViewModel.cargarDatos()
                    },
                    enabled = !ocupado
                ) {
                    Text("Actualizar pedidos")
                }

                if (pedidos.isEmpty()) {
                    Text("No existen pedidos registrados.")
                }

                // Muestra los pedidos de los compradores
                for (pedido in pedidos) {

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = "Pedido: ${pedido.numero}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text("Total: $${pedido.total}")
                            Text("Estado: ${pedido.estado}")

                            // Consulta los productos del pedido
                            OutlinedButton(
                                onClick = {
                                    gerenteViewModel.mostrarDetalles(
                                        pedido.id
                                    )
                                }
                            ) {
                                Text("Ver / ocultar detalle")
                            }

                            // Muestra detalles del pedido seleccionado
                            if (
                                gerenteViewModel.idDetalleAbierto.value ==
                                pedido.id
                            ) {

                                if (
                                    gerenteViewModel.cargandoDetalles.value
                                ) {

                                    Text("Cargando detalle...")

                                } else {

                                    for (
                                    detalle in
                                    gerenteViewModel.detallesPedido.value
                                    ) {

                                        Text(
                                            text = detalle.nombre +
                                                    " x${detalle.cantidad}"
                                        )

                                        Text(
                                            text = "Subtotal: " +
                                                    "$${detalle.precio * detalle.cantidad}"
                                        )
                                    }
                                }
                            }

                            // Permite cambiar el estado del pedido
                            if (
                                pedido.estado == "Confirmado" ||
                                pedido.estado == "Preparando" ||
                                pedido.estado == "Listo"
                            ) {

                                Button(
                                    onClick = {
                                        gerenteViewModel.avanzarPedido(
                                            pedido
                                        )
                                    },
                                    enabled = !ocupado
                                ) {
                                    Text("Avanzar estado")
                                }
                            }
                        }
                    }
                }
            }

            // REPORTES Y ESTADISTICAS
            "REPORTES" -> {

                Text(
                    text = "Reportes y estadisticas",
                    style = MaterialTheme.typography.titleLarge
                )

                Text("Productos registrados: ${productos.size}")
                Text("Productos con stock bajo: $bajoStock")
                Text("Pedidos registrados: ${pedidos.size}")
                Text("Pedidos pendientes: $pendientes")

                Text(
                    text = "Monto total de pedidos: $$montoPedidos"
                )

                // El monto no representa pagos bancarios reales
                Text(
                    text = "Este monto corresponde a pedidos " +
                            "registrados, no a pagos realizados."
                )

                Button(
                    onClick = {
                        gerenteViewModel.cargarDatos()
                    }
                ) {
                    Text("Actualizar reportes")
                }
            }
        }
    }
}

// Formulario reutilizable para productos
@Composable
fun FormularioProductoGerente(
    producto: ProductoEntity?,
    guardando: Boolean,
    onGuardar: (
        String,
        String,
        String,
        String,
        String
    ) -> Unit,
    onCancelar: () -> Unit
) {

    // Recupera los datos al editar un producto
    var nombre by remember(producto?.id) {
        mutableStateOf(producto?.nombre ?: "")
    }

    var precio by remember(producto?.id) {
        mutableStateOf(producto?.precio?.toString() ?: "")
    }

    var categoria by remember(producto?.id) {
        mutableStateOf(producto?.categoria ?: "Abarrotes")
    }

    var descripcion by remember(producto?.id) {
        mutableStateOf(producto?.descripcion ?: "")
    }

    var stock by remember(producto?.id) {
        mutableStateOf(producto?.stock?.toString() ?: "0")
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = if (producto == null) {
                "Agregar producto"
            } else {
                "Editar producto"
            },
            style = MaterialTheme.typography.titleLarge
        )

        // Nombre del producto
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Precio en pesos chilenos
        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio CLP") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Se utilizan las categorias actuales del catalogo
        Text(
            "Categorias: Abarrotes, Bebidas, Higiene, Lacteos"
        )

        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoria") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Descripcion del producto
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripcion") },
            modifier = Modifier.fillMaxWidth()
        )

        // Stock inicial o actualizado
        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Guarda los datos validados por el ViewModel
        Button(
            onClick = {
                onGuardar(
                    nombre,
                    precio,
                    categoria,
                    descripcion,
                    stock
                )
            },
            enabled = !guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (guardando) "Guardando..." else "Guardar producto"
            )
        }

        OutlinedButton(
            onClick = onCancelar,
            enabled = !guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}
