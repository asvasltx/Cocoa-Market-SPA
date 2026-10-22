package com.example.cocoa_market_spa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val precio: Int,
    val categoria: String,
    val descripcion: String,
    val stock: Int,
    val imagen: Int
)