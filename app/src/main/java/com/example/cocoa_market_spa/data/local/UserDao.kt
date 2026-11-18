
package com.example.cocoa_market_spa.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    // Registra un usuario en SQLite
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: User)

    // Busca un usuario por correo
    @Query(
        "SELECT * FROM users " +
                "WHERE LOWER(email) = LOWER(:email) LIMIT 1"
    )
    suspend fun getUserByEmail(email: String): User?

    // Comprueba correo y contraseña al iniciar sesion
    @Query(
        "SELECT * FROM users " +
                "WHERE LOWER(email) = LOWER(:email) " +
                "AND password = :password LIMIT 1"
    )
    suspend fun login(email: String, password: String): User?

    // Consulta si ya existe un gerente en este telefono
    @Query("SELECT COUNT(*) FROM users WHERE rol = 'GERENTE'")
    suspend fun contarGerentes(): Int
}
