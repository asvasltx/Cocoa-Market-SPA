package com.example.cocoa_market_spa.catalogo.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocoa_market_spa.R
import com.example.cocoa_market_spa.data.local.entity.ProductoDao
import com.example.cocoa_market_spa.data.local.entity.ProductoEntity
import kotlinx.coroutines.launch

class CatalogoViewModel(
    private val productoDao: ProductoDao
): ViewModel(){
    var productos = mutableStateOf<List<ProductoEntity>>(emptyList())
    private set

    var productoSeleccionado = mutableStateOf<ProductoEntity?>(null)
        private set

    fun cargarProductos(){
        viewModelScope.launch {
            if (productoDao.contarProductos()==0){
                val productosIniciales = listOf(
                    ProductoEntity(
                        nombre ="Arroz Tucapel 1kg",
                        precio = 1290,
                        categoria = "Abarrotes",
                        descripcion = "Arroz de 1 kg para preparar diferentes comidas.",
                        stock = 50,
                        imagen = R.drawable.arroztucapel
                    ),
                    ProductoEntity(
                        nombre ="Leche de chocolate colun 200ml",
                        precio = 1490,
                        categoria = "Bebidas",
                        descripcion = "Leche con sabor a chocolate en envase de 200 ml.",
                        stock = 20,
                        imagen = R.drawable.lechechocolate
                    ),
                    ProductoEntity(
                        nombre ="Aceite Maravilla 1L",
                        precio = 2000,
                        categoria = "Abarrotes",
                        descripcion = "Aceite de maravilla de 1 litro para cocinar.",
                        stock = 50,
                        imagen = R.drawable.aceite
                    ),
                    ProductoEntity(
                        nombre ="Pasta de dientes Pepsodent",
                        precio = 1000,
                        categoria = "Higiene",
                        descripcion = "Pasta dental para la higiene bucal diaria.",
                        stock = 50,
                        imagen = R.drawable.pastadedientes
                    )
                )
                for (producto in productosIniciales){
                    productoDao.insertarProducto(producto)
                }
            }
            productos.value = productoDao.obtenerProductos()
        }
    }

    fun cargarProductoPorId(id: Int){
        productoSeleccionado.value = null

        viewModelScope.launch {
            productoSeleccionado.value = productoDao.obtenerPorId(id)
        }
    }
}