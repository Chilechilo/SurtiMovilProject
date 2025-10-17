package com.surtiapp.surtimovil.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private data class TabItem(val titleRes: Int, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
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

    // 🌟 Estado de sesión
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var userName by rememberSaveable { mutableStateOf("Usuario") }

    // ✅ Detectar cuando regresamos del login
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collectLatest { entry ->
            val fromLogin = entry.destination.route == "home" &&
                    entry.savedStateHandle?.get<Boolean>("loggedIn") == true
            if (fromLogin) {
                isLoggedIn = true
                userName = entry.savedStateHandle?.get<String>("username") ?: "Usuario"
            }
        }
    }

    Scaffold(
        // ✅ Barra superior solo si hay sesión iniciada
        topBar = {
            if (isLoggedIn) {
                CenterAlignedTopAppBar(
                    title = { Text("SurtiMóvil") },
                    actions = {
                        IconButton(onClick = { selectedIndex = 4 }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Perfil",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        },

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
                0 -> CatalogoScreen()
                1 -> PedidosScreen()
                2 -> AyudaScreen()
                3 -> OfertasScreen()
                4 -> CuentasScreen(
                    navController = navController,
                    isLoggedIn = isLoggedIn,
                    onLogout = {
                        isLoggedIn = false
                        userName = ""
                    }
                )
            }
        }
    }
}

/* ======= Contenido de cada pestaña ======= */

@Composable
private fun CatalogoScreen() {
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://gist.githubusercontent.com/Manuel2210337/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api = remember { retrofit.create(HomeApi::class.java) }
    val repo = remember { HomeRepository(api) }

    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repo))
    val uiState by viewModel.ui.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchHome()
    }

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

/* ======= Pestaña de cuenta (login / logout dinámico) ======= */
@Composable
private fun CuentasScreen(
    navController: NavController,
    isLoggedIn: Boolean,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoggedIn) {
            // ✅ Pantalla de sesión iniciada
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Usuario",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = "Sesión iniciada",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
                )
                Button(
                    onClick = { onLogout() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cerrar sesión")
                }
            }
        } else {
            // ✅ Pantalla de invitado (no logueado)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Invitado",
                    modifier = Modifier.size(80.dp)
                )

                var isNavigating by remember { mutableStateOf(false) }

                Button(
                    onClick = {
                        if (!isNavigating) {
                            isNavigating = true
                            navController.navigate("login")
                        }
                    },
                    enabled = !isNavigating
                ) {
                    if (isNavigating) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 8.dp)
                        )
                        Text("Abriendo...")
                    } else {
                        Text("Iniciar sesión")
                    }
                }
            }
        }
    }
}

/* ======= Tarjeta genérica reutilizable ======= */
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
