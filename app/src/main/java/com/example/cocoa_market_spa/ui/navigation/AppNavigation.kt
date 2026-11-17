
package com.example.cocoa_market_spa.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cocoa_market_spa.catalogo.CatalogoNavigation
import com.example.cocoa_market_spa.gerente.GerenteScreen
import com.example.cocoa_market_spa.ui.auth.AuthViewModel
import com.example.cocoa_market_spa.ui.auth.LoginScreen
import com.example.cocoa_market_spa.ui.auth.RegisterScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {

            LoginScreen(
                viewModel = authViewModel,

                onLoginSuccess = {

                    // Elegimos pantalla segun el rol del usuario
                    val destino = if (
                        authViewModel.rolUsuario.value == "GERENTE"
                    ) {
                        "gerente"
                    } else {
                        "home"
                    }

                    navController.navigate(destino) {
                        popUpTo("login") {
                            inclusive = true
                        }
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
                        popUpTo("register") {
                            inclusive = true
                        }
                    }
                },

                onNavigateToLogin = {
                    authViewModel.clearError()
                    navController.popBackStack()
                }
            )
        }

        // Pantalla del comprador
        composable("home") {

            CatalogoNavigation(
                nombreUsuario = authViewModel.nombreUsuario.value
            )
        }

        // Pantalla del gerente
        composable("gerente") {

            GerenteScreen(
                nombreGerente = authViewModel.nombreUsuario.value,

                onCerrarSesion = {
                    authViewModel.clearError()

                    navController.navigate("login") {
                        popUpTo("gerente") {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
