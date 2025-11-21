package com.surtiapp.surtimovil

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.surtiapp.surtimovil.core.datastore.DataStoreManager
import com.surtiapp.surtimovil.core.homescreen.model.network.HomeApi
import com.surtiapp.surtimovil.core.homescreen.repository.HomeRepository
import com.surtiapp.surtimovil.homescreen.home.login.HomeViewModelFactory
import com.surtiapp.surtimovil.homescreen.repository.CartRepository
import com.surtiapp.surtimovil.navigation.AppNavHost
import com.surtiapp.surtimovil.onboarding.viewmodel.OnboardingViewModel
import com.surtiapp.surtimovil.onboarding.views.OnboardingView
import com.surtiapp.surtimovil.ui.theme.SurtiMovilTheme
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val ds = DataStoreManager(this)

        val composeView = ComposeView(this).apply {
            setContent {
                SurtiMovilTheme {
                    val scope = rememberCoroutineScope()
                    val vm: OnboardingViewModel = viewModel()
                    val navController = rememberNavController()

                    // --- Inicialización de dependencias ---
                    val homeApi: HomeApi = remember {
                        getRetrofitInstance().create(HomeApi::class.java)
                    }

                    val homeRepository = remember { HomeRepository(homeApi) }

                    val cartRepository = remember { CartRepository }

                    val homeViewModelFactory = remember {
                        HomeViewModelFactory(
                            repo = homeRepository,
                            cartRepository = cartRepository
                        )
                    }
                    val onboardingDone: Boolean? by ds.onboardingDoneFlow.collectAsState(initial = null)

                    when (onboardingDone) {
                        null -> SplashLoader()
                        false -> OnboardingView(
                            viewModel = vm,
                            onFinish = { scope.launch { ds.setOnboardingDone(true) } }
                        )
                        true -> AppNavHost(
                            navController = navController,
                            activity = this@MainActivity,
                            homeViewModelFactory = homeViewModelFactory
                        )
                    }
                }
            }
        }

        // ✅ Asignamos el ComposeView como layout principal
        setContentView(composeView)
    }

    // --- Retrofit Instance ---
    private fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://gist.githubusercontent.com/Manuel2210337/") // Tu endpoint base
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

// --- Loader Composable ---
@Composable
fun SplashLoader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
