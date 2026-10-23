package com.example.cocoa_market_spa.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cocoa_market_spa.data.local.entity.ProductoDao
import com.example.cocoa_market_spa.data.local.entity.ProductoEntity
import com.example.cocoa_market_spa.model.CarritoModel
import com.example.cocoa_market_spa.data.local.entity.PedidoEntity
import com.example.cocoa_market_spa.data.local.entity.DetallePedidoEntity
import com.example.cocoa_market_spa.data.local.entity.PedidoDao

@Database(entities = [User::class, ProductoEntity::class,CarritoModel::class,PedidoEntity::class,
    DetallePedidoEntity::class],
    version = 3,
    exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun UserDao(): UserDao

    abstract fun ProductoDao(): ProductoDao

    abstract fun PedidoDao(): PedidoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val Migration_1_2 = object : Migration(1, 2) {

            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS productos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        precio INTEGER NOT NULL,
                        categoria TEXT NOT NULL,
                        descripcion TEXT NOT NULL,
                        stock INTEGER NOT NULL,
                        imagen INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }


        private val Migration_2_3 = object : Migration(2, 3) {

            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS carrito (
                productoId INTEGER NOT NULL PRIMARY KEY,
                nombre TEXT NOT NULL,
                precio INTEGER NOT NULL,
                cantidad INTEGER NOT NULL,
                imagen INTEGER NOT NULL
            )
            """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS pedidos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    numero TEXT NOT NULL,
                    total INTEGER NOT NULL,
                    estado TEXT NOT NULL
            )   
            """.trimIndent()
                )



        db.execSQL(
        """
            CREATE TABLE IF NOT EXISTS detalle_pedidos (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                pedidoId INTEGER NOT NULL,
                productoId INTEGER NOT NULL,
                nombre TEXT NOT NULL,
                precio INTEGER NOT NULL,
                cantidad INTEGER NOT NULL,
                imagen INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}


        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cocoa_market_db"
                )
                    .addMigrations(Migration_1_2,Migration_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

