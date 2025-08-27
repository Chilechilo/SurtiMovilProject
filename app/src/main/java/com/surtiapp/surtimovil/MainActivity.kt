package com.surtiapp.surtimovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.surtiapp.surtimovil.onboarding.viewmodel.OnboardingViewModel
import com.surtiapp.surtimovil.onboarding.views.OnboardingView
import com.surtiapp.surtimovil.ui.theme.SurtiMovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SurtiMovilTheme {
                val vm: OnboardingViewModel = viewModel()
                OnboardingView(viewModel = vm)
                // TabBarNavigationView()
                // Prueba Alex
            }
        }
    }
}