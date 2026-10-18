package com.example.cocoa_market_spa.catalogo.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocoa_market_spa.data.local.entity.ProductoDao
import com.example.cocoa_market_spa.data.local.entity.ProductoEntity
import kotlinx.coroutines.launch

class CatalogoViewModel(
    private val productoDao: ProductoDao
): ViewModel(){
    var productos = mutableStateOf<List<ProductoEntity>>(emptyList())
    private set

    fun cargarProductos(){
        viewModelScope.launch {
            productos.value = productoDao.obtenerProductos()
        }
    }
}