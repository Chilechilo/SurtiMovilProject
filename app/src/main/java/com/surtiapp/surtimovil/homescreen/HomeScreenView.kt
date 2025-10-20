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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.surtiapp.surtimovil.R
import com.surtiapp.surtimovil.homescreen.home.HomeViewModel
import com.surtiapp.surtimovil.homescreen.home.login.HomeViewModelFactory
import com.surtiapp.surtimovil.home.views.HomeViewProducts
import com.surtiapp.surtimovil.Addcarrito.viewmodel.CarritoViewModel

private data class TabItem(val titleRes: Int, val icon: ImageVector)

@Composable
fun HomeScreenView(
    navController: NavController,
    homeViewModelFactory: HomeViewModelFactory
) {
    val tabs = listOf(
        TabItem(R.string.tab_catalogo, Icons.Filled.List),
        TabItem(R.string.tab_pedidos, Icons.Filled.QrCode), // Vacío, reservado para QR
        TabItem(R.string.tab_ayuda, Icons.Filled.Help),
        TabItem(R.string.tab_ofertas, Icons.Filled.LocalOffer),
        TabItem(R.string.tab_mi_cuenta, Icons.Filled.Person),
    )

    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val carritoViewModel: CarritoViewModel = viewModel()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
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
                0 -> CatalogoScreen(
                    factory = homeViewModelFactory,
                    snackbarHostState = snackbarHostState,
                    carritoViewModel = carritoViewModel
                )
                1 -> PedidosScreen() // vacío, temporal
                2 -> AyudaScreen()
                3 -> OfertasScreen()
                4 -> CuentasScreen(navController)
            }
        }
    }
}

@Composable
private fun CatalogoScreen(
    factory: HomeViewModelFactory,
    snackbarHostState: SnackbarHostState,
    carritoViewModel: CarritoViewModel
) {
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val uiState by viewModel.ui.collectAsState()
    val localUserId = remember { "local_user_001" }

    LaunchedEffect(Unit) {
        viewModel.setUserId(localUserId)
        viewModel.fetchHome()
    }

    LaunchedEffect(key1 = uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
            viewModel.clearMessage()
        }
    }

    HomeViewProducts(
        uiState = uiState,
        viewModel = viewModel,
        carritoViewModel = carritoViewModel
    )
}

@Composable
private fun PedidosScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Aquí irá el lector de QR 📷",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun AyudaScreen() {
    CenterCard(
        title = stringResource(R.string.tab_ayuda),
        body = stringResource(R.string.ayuda_body)
    )
}

@Composable
private fun OfertasScreen() {
    CenterCard(
        title = stringResource(R.string.tab_ofertas),
        body = stringResource(R.string.ofertas_body)
    )
}

@Composable
private fun CuentasScreen(navController: NavController) {
    CenterCard(
        title = stringResource(R.string.tab_mi_cuenta),
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
