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
import com.surtiapp.surtimovil.R

// Ahora los tabs guardan IDs en vez de String directo
private data class TabItem(val titleRes: Int, val icon: ImageVector)

@Composable
fun HomeScreenView(navController: NavController) {
    val tabs = listOf(
        TabItem(R.string.tab_catalogo, Icons.Filled.List),
        TabItem(R.string.tab_pedidos, Icons.Filled.ShoppingCart),
        TabItem(R.string.tab_mi_cuenta, Icons.Filled.Person),
        TabItem(R.string.tab_ayuda, Icons.Filled.Help),
        TabItem(R.string.tab_ofertas, Icons.Filled.LocalOffer),
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
            Text(stringResource(R.string.go_login))
        }
    }
}

@Composable
private fun PedidosScreen() {
    CenterCard(
        title = stringResource(R.string.pedidos_title),
        body = stringResource(R.string.pedidos_body)
    )
}

@Composable
private fun CuentasScreen() {
    CenterCard(
        title = stringResource(R.string.cuenta_title),
        body = stringResource(R.string.cuenta_body)
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
