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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.surtiapp.surtimovil.core.homescreen.model.network.HomeApi
import com.surtiapp.surtimovil.core.datastore.DataStoreManager
import com.surtiapp.surtimovil.core.homescreen.repository.HomeRepository
import com.surtiapp.surtimovil.homescreen.home.login.HomeViewModelFactory
import com.surtiapp.surtimovil.homescreen.repository.CartRepository
import com.surtiapp.surtimovil.navigation.AppNavHost
import com.surtiapp.surtimovil.onboarding.viewmodel.OnboardingViewModel
import com.surtiapp.surtimovil.onboarding.views.OnboardingView
import com.surtiapp.surtimovil.ui.theme.SurtiMovilTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val ds = DataStoreManager(this)

        setContent {
            SurtiMovilTheme {
                val scope = rememberCoroutineScope()
                val vm: OnboardingViewModel = viewModel()
                val navController = rememberNavController()

                // --- INICIALIZACIÓN DE DEPENDENCIAS CORREGIDA Y USANDO 'remember' ---

                // 1. Instancia de Retrofit API (DEBES DEFINIR getRetrofitInstance() en tu proyecto)
                // Usamos 'remember' para no recrear el objeto en cada recomposición.
                val homeApi: HomeApi = remember {
                    // REEMPLAZA ESTO: Asume que tienes una función global que devuelve la interfaz.
                    getRetrofitInstance().create(HomeApi::class.java)
                }

                // 2. Instancia de Firestore
                val firestoreInstance = remember {
                    FirebaseFirestore.getInstance()
                }

                // 3. Repositorios y Factory
                val homeRepository = remember {
                    HomeRepository(homeApi)
                }
                val cartRepository = remember {
                    CartRepository(firestoreInstance)
                }
                val homeViewModelFactory = remember {
                    HomeViewModelFactory(homeRepository, cartRepository)
                }

                // --- FIN DE LA INICIALIZACIÓN ---


                val onboardingDone: Boolean? by ds.onboardingDoneFlow.collectAsState(initial = null)

                when (onboardingDone) {
                    null -> SplashLoader()
                    false -> OnboardingView(
                        viewModel = vm,
                        onFinish = {
                            scope.launch { ds.setOnboardingDone(true) }
                        }
                    )
                    // NOTA: Cuando pasas la Factory al AppNavHost, los ViewModels en ese host
                    // deberán usarla para obtener las dependencias (como HomeViewModel).
                    true -> AppNavHost(
                        navController = navController,
                        homeViewModelFactory = homeViewModelFactory // <-- Posiblemente necesites pasarla aquí
                    )
                }
            }
        }
    }

    // Función de ejemplo. DEBES implementarla en tu proyecto.
    private fun getRetrofitInstance(): retrofit2.Retrofit {
        // Implementación de tu cliente Retrofit (ejemplo, ajusta según tu código)
        // val client = OkHttpClient.Builder()...
        // return Retrofit.Builder()...
        TODO("Debes implementar la inicialización de Retrofit aquí o en un objeto/clase separada.")
    }
}

@Composable
private fun SplashLoader() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}