
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

    // Navegacion general de la aplicacion.
    val navController = rememberNavController()

    // Autenticacion local mediante Room.
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

                        launchSingleTop = true
                    }
                },

                onNavigateToRegister = {

                    authViewModel.clearError()
                    navController.navigate("register")
                }
            )
        }

        // REGISTRO EXCLUSIVO DE COMPRADORES
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

        // PANTALLAS DEL COMPRADOR
        composable("home") {

            CatalogoNavigation(
                nombreUsuario =
                    authViewModel.nombreUsuario.value,

                // Identifica al propietario de los pedidos.
                correoUsuario =
                    authViewModel.correoUsuario.value,

                onCerrarSesion = {

                    authViewModel.cerrarSesion()

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
                nombreGerente =
                    authViewModel.nombreUsuario.value,

                onCerrarSesion = {

                    authViewModel.cerrarSesion()

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
