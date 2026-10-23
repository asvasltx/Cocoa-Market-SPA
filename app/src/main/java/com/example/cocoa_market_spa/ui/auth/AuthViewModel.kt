package com.example.cocoa_market_spa.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocoa_market_spa.data.local.AppDatabase
import com.example.cocoa_market_spa.data.local.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).UserDao()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun register(name: String, email: String, pass: String, onSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Por favor completa todos los campos"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Ingresa un correo electrónico válido"
            return
        }
        if (pass.length < 4) {
            _errorMessage.value = "La contraseña debe tener al menos 4 caracteres"
            return
        }

        viewModelScope.launch {
            val existing = userDao.getUserByEmail(email)
            if (existing != null) {
                _errorMessage.value = "Este correo ya está registrado"
            } else {
                userDao.registerUser(User(nombre = name, email = email, password = pass))
                _errorMessage.value = null
                onSuccess()
            }
        }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Ingresa tu correo y contraseña"
            return
        }

        viewModelScope.launch {
            val user = userDao.login(email, pass)
            if (user != null) {
                _errorMessage.value = null
                onSuccess()
            } else {
                _errorMessage.value = "Correo o contraseña incorrectos"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}