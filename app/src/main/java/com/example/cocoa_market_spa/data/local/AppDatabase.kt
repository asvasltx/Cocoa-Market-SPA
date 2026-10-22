package com.example.cocoa_market_spa.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cocoa_market_spa.data.local.entity.ProductoDao
import com.example.cocoa_market_spa.data.local.entity.ProductoEntity

@Database(entities = [User::class, ProductoEntity::class],
    version = 2,
    exportSchema = false)

abstract class AppDatabase : RoomDatabase(){
    abstract fun UserDao(): UserDao

    abstract fun ProductoDao(): ProductoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val Migration_1_2 = object : Migration(1,2){

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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cocoa_market_db"
                )
                    .addMigrations(Migration_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

