package com.surtiapp.surtimovil.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.surtiapp.surtimovil.R
import com.surtiapp.surtimovil.core.homescreen.model.network.HomeApi
import com.surtiapp.surtimovil.core.homescreen.repository.HomeRepository
import com.surtiapp.surtimovil.homescreen.home.HomeViewModel
import com.surtiapp.surtimovil.homescreen.home.login.HomeViewModelFactory
import com.surtiapp.surtimovil.homescreen.home.views.HomeViewProducts
import retrofit2.Retrofit // <-- NUEVO
import retrofit2.converter.gson.GsonConverterFactory // <-- NUEVO

// Ahora los tabs guardan IDs en vez de String directo
private data class TabItem(val titleRes: Int, val icon: ImageVector)

@Composable
fun HomeScreenView(navController: NavController) {
    val tabs = listOf(
        TabItem(R.string.tab_catalogo, Icons.Filled.List),
        TabItem(R.string.tab_pedidos, Icons.Filled.ShoppingCart),
        TabItem(R.string.tab_ayuda, Icons.Filled.Help),
        TabItem(R.string.tab_ofertas, Icons.Filled.LocalOffer),
        TabItem(R.string.tab_mi_cuenta, Icons.Filled.Person),
    )

    var selectedIndex by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(item.icon, contentDescription = stringResource(item.titleRes)) },
                        label = { Text(stringResource(item.titleRes)) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedIndex) {
                0 -> CatalogoScreen() // Aquí llamamos a la función actualizada con la lógica de productos
                1 -> PedidosScreen()
                2 -> AyudaScreen()
                3 -> OfertasScreen()
                4 -> CuentasScreen(navController)
            }
        }
    }
}

/* ======= Contenido de cada pestaña ======= */

@Composable
private fun CatalogoScreen() {
    // ----------------------------------------------------
    // Lógica para cargar el Catálogo de Productos (ÍNDICE 0)
    // ----------------------------------------------------

    // 1. Inicialización de Retrofit y Repositorio (usamos remember para que solo se haga una vez)
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://gist.githubusercontent.com/Manuel2210337/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api = remember { retrofit.create(HomeApi::class.java) }
    val repo = remember { HomeRepository(api) }

    // 2. Inicialización del ViewModel
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repo))

    // 3. Observar el estado de la UI
    val uiState by viewModel.ui.collectAsState()

    // 4. Iniciar la carga de datos (solo al entrar a la pantalla)
    LaunchedEffect(Unit) {
        viewModel.fetchHome()
    }

    // 5. Renderizar la vista real de productos
    HomeViewProducts(uiState = uiState)
}

@Composable
private fun PedidosScreen() {
    CenterCard(
        title = stringResource(R.string.pedidos_title),
        body = stringResource(R.string.pedidos_body)
    )
}

@Composable
private fun AyudaScreen() {
    CenterCard(
        title = stringResource(R.string.ayuda_title),
        body = stringResource(R.string.ayuda_body)
    )
}

@Composable
private fun OfertasScreen() {
    CenterCard(
        title = stringResource(R.string.ofertas_title),
        body = stringResource(R.string.ofertas_body)
    )
}

@Composable
private fun CuentasScreen(navController: NavController) {
    CenterCard(
        title = stringResource(R.string.cuenta_title),
        body = stringResource(R.string.cuenta_body)
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { navController.navigate("login") }) {
            Text(stringResource(R.string.go_login))
        }
    }
}

@Composable
private fun CenterCard(title: String, body: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(text = body, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
