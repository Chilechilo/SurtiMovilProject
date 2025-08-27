package com.surtiapp.surtimovil.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector

// Data de los tabs
private data class TabItem(val title: String, val icon: ImageVector)

@Composable
fun HomeScreenView() {
    // Items del bottom bar
    val tabs = listOf(
        TabItem("Catálogo", Icons.Filled.List),
        TabItem("Pedidos", Icons.Filled.ShoppingCart),
        TabItem("Mi Cuenta", Icons.Filled.Person),
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
                0 -> CatalogoScreen()
                1 -> PedidosScreen()
                2 -> CuentasScreen()
            }
        }
    }
}

/* ======= Contenido de cada pestaña (placeholders bonitos) ======= */

@Composable
private fun CatalogoScreen() {
    CenterCard(
        title = "Catálogo",
        body = "Bienvenido Corly"
    )
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
