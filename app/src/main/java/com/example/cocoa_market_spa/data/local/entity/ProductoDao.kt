
package com.example.cocoa_market_spa.data.local.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductoDao {

    // Guarda un producto
    @Insert
    suspend fun insertarProducto(producto: ProductoEntity)

    // Consulta todos los productos
    @Query("SELECT * FROM productos")
    suspend fun obtenerProductos(): List<ProductoEntity>

    // Busca productos por categoria
    @Query("SELECT * FROM productos WHERE categoria = :categoria")
    suspend fun obtenerPorCategoria(categoria: String): List<ProductoEntity>

    // Busca un producto por ID
    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): ProductoEntity?

    // Cuenta los productos
    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contarProductos(): Int

    // Permite actualizar los datos de un producto
    @Update
    suspend fun actualizarProducto(producto: ProductoEntity)

    // Descuenta stock solamente si hay unidades suficientes
    // Devuelve 1 si se actualizo y 0 si no habia stock
    @Query("UPDATE productos SET stock = stock - :cantidad WHERE id = :id AND stock >= :cantidad")
    suspend fun descontarStock(id: Int, cantidad: Int): Int
}
