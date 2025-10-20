package com.surtiapp.surtimovil.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.surtiapp.surtimovil.homescreen.HomeScreenView
import com.surtiapp.surtimovil.homescreen.home.login.HomeViewModelFactory // <-- Importación necesaria
import com.surtiapp.surtimovil.login.views.LoginView

@Composable
fun AppNavHost(
    navController: NavHostController,
    // ¡Añadir el factory para inyectar HomeViewModel!
    homeViewModelFactory: HomeViewModelFactory
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            // ¡Pasar el factory a la vista principal!
            HomeScreenView(
                navController = navController,
                homeViewModelFactory = homeViewModelFactory
            )
        }
        composable("login") { LoginView(navController) }
    }
}
