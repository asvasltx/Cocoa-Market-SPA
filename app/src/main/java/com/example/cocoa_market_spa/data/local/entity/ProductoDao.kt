
package com.example.cocoa_market_spa.data.local.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductoDao {

    // Guarda un producto nuevo
    @Insert
    suspend fun insertarProducto(producto: ProductoEntity)

    // Consulta todos los productos
    @Query("SELECT * FROM productos")
    suspend fun obtenerProductos(): List<ProductoEntity>

    // Consulta productos por categoria
    @Query("SELECT * FROM productos WHERE categoria = :categoria")
    suspend fun obtenerPorCategoria(categoria: String): List<ProductoEntity>

    // Busca un producto por su identificador
    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): ProductoEntity?

    // Cuenta los productos registrados
    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contarProductos(): Int

    // Actualiza todos los datos de un producto
    @Update
    suspend fun actualizarProducto(producto: ProductoEntity)

    // Descuenta stock al confirmar una compra
    // Solo funciona si existen unidades suficientes
    @Query(
        "UPDATE productos SET stock = stock - :cantidad " +
                "WHERE id = :id AND stock >= :cantidad"
    )
    suspend fun descontarStock(id: Int, cantidad: Int): Int

    // Permite aumentar o disminuir el stock desde gerencia
    // Nunca deja cantidades negativas
    @Query(
        "UPDATE productos SET stock = stock + :cambio " +
                "WHERE id = :id AND stock + :cambio >= 0"
    )
    suspend fun cambiarStock(id: Int, cambio: Int): Int
}
