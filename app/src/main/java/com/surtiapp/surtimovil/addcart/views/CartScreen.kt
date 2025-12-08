package com.surtiapp.surtimovil.addcart.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.surtiapp.surtimovil.addcart.viewmodel.CartViewModel
import com.surtiapp.surtimovil.core.orders.viewmodel.OrdersViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    ordersViewModel: OrdersViewModel,
    userToken: String?,          // token JWT del usuario logueado
    onBack: () -> Unit
) {
    val productos by viewModel.productosEnCarrito.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi carrito") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (productos.isEmpty()) {
                Text(
                    text = "Tu carrito está vacío 🛒",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(productos) { producto ->
                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            producto.nombre,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            "$${producto.precio} c/u",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("x${producto.cantidadEnCarrito}")
                                        IconButton(
                                            onClick = { viewModel.removeFromCart(producto) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Eliminar"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider()

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total:", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "$${productos.sumOf { it.precio * it.cantidadEnCarrito }}",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Button(
                            onClick = {
                                val token = userToken.orEmpty()
                                if (token.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Debes iniciar sesión para finalizar el pedido",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                    return@Button
                                }

                                scope.launch {
                                    val (ok, msg) = ordersViewModel.createOrderFromCart(
                                        productosCarrito = productos,
                                        token = token
                                    )

                                    if (ok) {
                                        snackbarHostState.showSnackbar(
                                            message = "¡Pedido realizado con éxito! 🛒✨",
                                            duration = SnackbarDuration.Short
                                        )
                                        viewModel.clearCarrito()
                                        onBack()
                                    } else {
                                        snackbarHostState.showSnackbar(
                                            message = msg ?: "No se pudo crear el pedido",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Text("Proceder al pago")
                        }
                    }
                }
            }
        }
    }
}
