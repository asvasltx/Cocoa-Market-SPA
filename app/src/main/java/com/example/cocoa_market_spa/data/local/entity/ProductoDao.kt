package com.example.cocoa_market_spa.data.local.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductoDao{
    @Insert
    suspend fun insertarProducto(producto: ProductoEntity)

    @Query("SELECT * FROM productos")
    suspend fun obtenerProductos(): List<ProductoEntity>

    @Query ("SELECT * FROM productos WHERE categoria = :categoria")
    suspend fun  obtenerPorCategoria(categoria: String): List<ProductoEntity>

    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): ProductoEntity?

    @Query("SELECT COUNT (*) FROM productos")
    suspend fun contarProductos(): Int
}