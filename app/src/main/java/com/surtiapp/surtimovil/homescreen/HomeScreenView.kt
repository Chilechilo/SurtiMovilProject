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

// *** IMPORTACIÓN NECESARIA PARA FIREBASE AUTH ***
import com.google.firebase.auth.FirebaseAuth
// ***********************************************

import com.surtiapp.surtimovil.R
import com.surtiapp.surtimovil.homescreen.home.HomeViewModel
import com.surtiapp.surtimovil.homescreen.home.login.HomeViewModelFactory
import androidx.compose.ui.graphics.Color
// IMPORTACIÓN CORREGIDA: Asumiendo que 'HomeViewProducts' está aquí, ajusta si es necesario
import com.surtiapp.surtimovil.home.views.HomeViewProducts
import kotlinx.coroutines.launch // Necesario para el scope.launch si se usa

private data class TabItem(val titleRes: Int, val icon: ImageVector)

@Composable
fun HomeScreenView(
    navController: NavController,
    homeViewModelFactory: HomeViewModelFactory
) {
    val tabs = listOf(
        TabItem(R.string.tab_catalogo, Icons.Filled.List),
        TabItem(R.string.tab_pedidos, Icons.Filled.ShoppingCart),
        TabItem(R.string.tab_ayuda, Icons.Filled.Help),
        TabItem(R.string.tab_ofertas, Icons.Filled.LocalOffer),
        TabItem(R.string.tab_mi_cuenta, Icons.Filled.Person),
    )

    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
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
                0 -> CatalogoScreen(factory = homeViewModelFactory, snackbarHostState = snackbarHostState)
                1 -> PedidosScreen()
                2 -> AyudaScreen()
                3 -> OfertasScreen()
                4 -> CuentasScreen(navController)
            }
        }
    }
}

// --------------------------------------------------

@Composable
private fun CatalogoScreen(
    factory: HomeViewModelFactory,
    snackbarHostState: SnackbarHostState
) {
    // 1. Inicialización ÚNICA del ViewModel usando la Factory inyectada
    val viewModel: HomeViewModel = viewModel(factory = factory)

    // 2. Observar el estado de la UI (usando 'uiState' o el nombre que tenga tu StateFlow)
    val uiState by viewModel.ui.collectAsState()

    // 3. Obtener el scope para llamadas que no son Compose (aunque LaunchedEffect ya lo da)
    // Lo mantendremos simple, usando el contexto de LaunchedEffect
    // val scope = rememberCoroutineScope() // No es necesario si no lo usas

    // 4. Lógica de inicio y manejo de datos (Solo se ejecuta una vez al inicio)
    LaunchedEffect(Unit) {
        // A. OBTENER ID DEL USUARIO DE FIREBASE AUTH
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val userId = firebaseUser?.uid ?: "anonymous_user"

        // ❌ Se eliminaron las líneas duplicadas de viewModel y uiState aquí dentro. ❌

        // B. ESTABLECER EL ID EN EL VIEWMODEL para usar en Firestore
        viewModel.setUserId(userId)

        // C. INICIAR LA CARGA DE DATOS
        viewModel.fetchHome()
    }

    // 5. Observar los mensajes del ViewModel y mostrar Snackbar
    LaunchedEffect(key1 = uiState.message) {
        uiState.message?.let { message ->
            // Ejecuta la función suspend (showSnackbar) directamente en el contexto LaunchedEffect
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
            // Lógica simple para limpiar el estado (Accede al viewModel global)
            viewModel.clearMessage()
        }
    } // ⚠️ Cierre CORRECTO del LaunchedEffect ⚠️

    // 6. Renderizar la vista real de productos
    // ¡Este código estaba fuera de la función CatalogoScreen debido a un cierre de llave extra!
    HomeViewProducts(uiState = uiState, viewModel = viewModel)
}


@Composable
private fun PedidosScreen() {
    CenterCard(
        title = stringResource(R.string.tab_pedidos),
        body = stringResource(R.string.pedidos_body)
    )
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