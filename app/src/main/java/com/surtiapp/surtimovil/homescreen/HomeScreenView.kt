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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

// Lista de tabs
private data class TabItem(val title: String, val icon: ImageVector)

@Composable
fun HomeScreenView(navController: NavController) {
    val tabs = listOf(
        TabItem("Catálogo", Icons.Filled.List),
        TabItem("Pedidos", Icons.Filled.ShoppingCart),
        TabItem("Mi Cuenta", Icons.Filled.Person),
        TabItem("Ayuda", Icons.Filled.Help),
        TabItem("Ofertas", Icons.Filled.LocalOffer),
    )

    var selectedIndex by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
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
                0 -> CatalogoScreen(navController)
                1 -> PedidosScreen()
                2 -> CuentasScreen()
                3 -> AyudaScreen()
                4 -> OfertasScreen()
            }
        }
    }
}

/* ======= Contenido de cada pestaña ======= */

@Composable
private fun CatalogoScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { navController.navigate("login") }) {
            Text("Ir al Login")
        }
    }
}

@Composable
private fun PedidosScreen() {
    CenterCard(
        title = "Pedidos",
        body = "My name is Edwin, I made the mimic"
    )
}

@Composable
private fun CuentasScreen() {
    CenterCard(
        title = "Mi Cuenta",
        body = "Can I get a glass of water please?"
    )
}

@Composable
private fun AyudaScreen() {
    CenterCard(
        title = "Ayuda",
        body = "Preguntas frecuentes"
    )
}

@Composable
private fun OfertasScreen() {
    CenterCard(
        title = "Ofertas",
        body = "Ofertas de temporada"
    )
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