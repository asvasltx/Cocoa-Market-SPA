package com.example.cocoa_market_spa.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cocoa_market_spa.ui.auth.AuthViewModel
import com.example.cocoa_market_spa.ui.auth.LoginScreen
import com.example.cocoa_market_spa.ui.auth.RegisterScreen
import com.example.cocoa_market_spa.catalogo.CatalogoNavigation

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    authViewModel.clearError()
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegistered = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    authViewModel.clearError()
                    navController.popBackStack()
                }
            )
        }

        composable("home") {
            CatalogoNavigation(
                nombreUsuario = authViewModel.nombreUsuario.value
            )
        }
    }
}