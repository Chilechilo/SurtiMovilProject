package com.surtiapp.surtimovil.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.surtiapp.surtimovil.homescreen.HomeScreenView
import com.surtiapp.surtimovil.login.views.LoginView

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreenView(navController) }
        composable("login") { LoginView(navController) }
    }
}