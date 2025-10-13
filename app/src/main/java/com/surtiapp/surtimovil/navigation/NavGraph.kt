package com.surtiapp.surtimovil.navigation

import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.surtiapp.surtimovil.Signin.views.SigninView
import com.surtiapp.surtimovil.homescreen.HomeScreenView
import com.surtiapp.surtimovil.login.views.LoginView
import android.util.Log

@Composable
fun AppNavHost(
    navController: NavHostController,
    activity: FragmentActivity
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreenView(navController)
        }

        composable("signin") {
            SigninView(navController)
        }

        composable("login") {
            Log.d("BiometricCheck", "✅ Activity pasada desde MainActivity: ${activity.localClassName}")
            LoginView(navController, activity)
        }
    }
}
