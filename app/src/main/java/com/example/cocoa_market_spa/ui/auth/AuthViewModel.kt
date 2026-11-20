
package com.example.cocoa_market_spa.ui.auth

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocoa_market_spa.data.local.AppDatabase
import com.example.cocoa_market_spa.data.local.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) :
    AndroidViewModel(application) {

    var nombreUsuario = mutableStateOf("")
        private set

    // Correo del usuario que inicio sesion.
    var correoUsuario = mutableStateOf("")
        private set

    // Rol utilizado para navegar a la pantalla correcta.
    var rolUsuario = mutableStateOf("CLIENTE")
        private set

    private val userDao =
        AppDatabase.getDatabase(application).UserDao()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage = _errorMessage.asStateFlow()

    // Solamente registra compradores.
    fun register(
        name: String,
        email: String,
        pass: String,
        onSuccess: () -> Unit
    ) {

        if (name.isBlank() ||
            email.isBlank() ||
            pass.isBlank()
        ) {
            _errorMessage.value =
                "Por favor completa todos los campos"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email.trim()).matches()
        ) {
            _errorMessage.value =
                "Ingresa un correo electrónico válido"
            return
        }

        if (pass.length < 8) {
            _errorMessage.value =
                "La contraseña debe tener al menos 8 caracteres"
            return
        }

        if (!pass.any { it.isLetter() }) {
            _errorMessage.value =
                "La contraseña debe contener al menos una letra"
            return
        }

        if (!pass.any { it.isDigit() }) {
            _errorMessage.value =
                "La contraseña debe contener al menos un número"
            return
        }

        if (!pass.any { !it.isLetterOrDigit() && !it.isWhitespace() }) {
            _errorMessage.value =
                "La contraseña debe contener al menos un símbolo (@, #, !, etc.)"
            return
        }

        viewModelScope.launch {

            val correo = email.trim()
            val existing = userDao.getUserByEmail(correo)

            if (existing != null) {

                _errorMessage.value =
                    "Este correo ya está registrado"

            } else {

                userDao.registerUser(
                    User(
                        nombre = name.trim(),
                        email = correo,
                        password = pass
                    )
                )

                _errorMessage.value = null
                onSuccess()
            }
        }
    }

    // Inicia sesion con un usuario existente en SQLite.
    fun login(
        email: String,
        pass: String,
        onSuccess: () -> Unit
    ) {

        if (email.isBlank() || pass.isBlank()) {
            _errorMessage.value =
                "Ingresa tu correo y contraseña"
            return
        }

        viewModelScope.launch {

            val user = userDao.login(
                email.trim(),
                pass
            )

            if (user != null) {

                nombreUsuario.value = user.nombre
                correoUsuario.value = user.email
                rolUsuario.value = user.rol

                _errorMessage.value = null
                onSuccess()

            } else {

                _errorMessage.value =
                    "Correo o contraseña incorrectos"
            }
        }
    }

    // Limpia los datos del usuario al cerrar sesion.
    fun cerrarSesion() {

        nombreUsuario.value = ""
        correoUsuario.value = ""
        rolUsuario.value = "CLIENTE"

        clearError()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
