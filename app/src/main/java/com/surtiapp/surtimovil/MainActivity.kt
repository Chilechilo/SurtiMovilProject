package com.surtiapp.surtimovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.surtiapp.surtimovil.core.datastore.DataStoreManager
import com.surtiapp.surtimovil.navigation.AppNavHost
import com.surtiapp.surtimovil.onboarding.viewmodel.OnboardingViewModel
import com.surtiapp.surtimovil.onboarding.views.OnboardingView
import com.surtiapp.surtimovil.ui.theme.SurtiMovilTheme
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val ds = DataStoreManager(this)

        setContent {
            SurtiMovilTheme {
                val scope = rememberCoroutineScope()
                val vm: OnboardingViewModel = viewModel()

                val onboardingDone: Boolean? by ds.onboardingDoneFlow.collectAsState(initial = null)
                val navController = rememberNavController()

                when (onboardingDone) {
                    null -> SplashLoader()
                    false -> OnboardingView(
                        viewModel = vm,
                        onFinish = {
                            scope.launch { ds.setOnboardingDone(true) }
                        }
                    )
                    true -> AppNavHost(
                        navController = navController,
                        activity = this
                    )
                }
            }
        }
    }
}

@Composable
private fun SplashLoader() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
