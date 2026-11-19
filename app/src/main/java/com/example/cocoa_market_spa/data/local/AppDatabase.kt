
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

@Database(
    entities = [
        User::class,
        ProductoEntity::class,
        CarritoModel::class,
        PedidoEntity::class,
        DetallePedidoEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun UserDao(): UserDao
    abstract fun ProductoDao(): ProductoDao
    abstract fun PedidoDao(): PedidoDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Version 1 a 2: tabla de productos.
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

        // Version 2 a 3: carrito y pedidos.
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

        // Version 3 a 4: roles de usuario.
        private val Migration_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE users ADD COLUMN rol " +
                            "TEXT NOT NULL DEFAULT 'CLIENTE'"
                )
            }
        }

        // Version 4 a 5: nuevos datos de los pedidos.
        // No elimina los pedidos existentes.
        private val Migration_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    "ALTER TABLE pedidos ADD COLUMN " +
                            "usuarioEmail TEXT NOT NULL DEFAULT ''"
                )

                db.execSQL(
                    "ALTER TABLE pedidos ADD COLUMN " +
                            "tipoEntrega TEXT NOT NULL DEFAULT 'Retiro en tienda'"
                )

                db.execSQL(
                    "ALTER TABLE pedidos ADD COLUMN " +
                            "direccion TEXT NOT NULL DEFAULT ''"
                )

                db.execSQL(
                    "ALTER TABLE pedidos ADD COLUMN " +
                            "metodoPago TEXT NOT NULL DEFAULT 'Efectivo'"
                )

                db.execSQL(
                    "ALTER TABLE pedidos ADD COLUMN " +
                            "costoDespacho INTEGER"
                )
            }
        }

        // Devuelve una unica instancia de la base de datos.
        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cocoa_market_db"
                )
                    .addMigrations(
                        Migration_1_2,
                        Migration_2_3,
                        Migration_3_4,
                        Migration_4_5
                    )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
