package com.example.cocoa_market_spa.data.local
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.example.cocoa_market_spa.model.CarritoModel

@Dao
interface CarritoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarProducto(producto: CarritoModel)

    @Query("SELECT * FROM carrito")
    suspend fun obtenerProductos(): List<CarritoModel>

    @Query("DELETE FROM carrito WHERE productoId = :productoId")
    suspend fun eliminarProducto(productoId: Int)

    @Query(
        "UPDATE carrito SET cantidad = :cantidad " +
                "WHERE productoId = :productoId"
    )
    suspend fun actualizarCantidad(
        productoId: Int,
        cantidad: Int
    )

    @Query("DELETE FROM carrito")
    suspend fun vaciarCarrito()
}