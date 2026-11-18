
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

    // Controla la navegacion general
    val navController = rememberNavController()

    // Mantiene la autenticacion existente en SQLite
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // INICIO DE SESION
        composable("login") {

            LoginScreen(
                viewModel = authViewModel,

                onLoginSuccess = {

                    // Abre la pantalla segun el rol
                    val destino = if (
                        authViewModel.rolUsuario.value == "GERENTE"
                    ) {
                        "gerente"
                    } else {
                        "home"
                    }

                    navController.navigate(destino) {

                        // Elimina el login anterior
                        popUpTo("login") {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },

                onNavigateToRegister = {
                    authViewModel.clearError()
                    navController.navigate("register")
                }
            )
        }

        // REGISTRO DE USUARIOS
        composable("register") {

            RegisterScreen(
                viewModel = authViewModel,

                onRegistered = {

                    // Regresa al login despues del registro
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

        // PANTALLAS DEL COMPRADOR
        composable("home") {

            CatalogoNavigation(
                nombreUsuario = authViewModel.nombreUsuario.value,

                // NUEVO: permite cerrar sesion como cliente
                onCerrarSesion = {

                    authViewModel.clearError()

                    // Regresa al login y elimina las pantallas
                    // anteriores del comprador
                    navController.navigate("login") {

                        popUpTo("home") {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }

        // PANEL DEL GERENTE
        composable("gerente") {

            GerenteScreen(
                nombreGerente = authViewModel.nombreUsuario.value,

                onCerrarSesion = {

                    authViewModel.clearError()

                    // Mantiene el cierre de sesion del gerente
                    navController.navigate("login") {

                        popUpTo("gerente") {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
