package com.surtiapp.surtimovil.home.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.surtiapp.surtimovil.Addcarrito.viewmodel.CarritoViewModel
import com.surtiapp.surtimovil.homescreen.home.HomeUiState
import com.surtiapp.surtimovil.homescreen.home.HomeViewModel
import com.surtiapp.surtimovil.homescreen.model.dto.Category
import com.surtiapp.surtimovil.homescreen.model.dto.Product
import androidx.compose.foundation.BorderStroke
import kotlinx.coroutines.launch

@Composable
fun HomeViewProducts(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    carritoViewModel: CarritoViewModel,
    modifier: Modifier = Modifier
) {
    val productosCarrito by carritoViewModel.productosEnCarrito.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(
                            text = uiState.error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        // 🔹 Mostrar categorías y productos
                        items(uiState.categorias) { category ->
                            CategoryRow(
                                category = category,
                                onAddToCart = { product ->
                                    // Crear objeto del carrito con ID único
                                    val productoCarrito =
                                        com.surtiapp.surtimovil.Addcarrito.model.Producto(
                                            id = "${category.categoria}_${product.id}",
                                            nombre = product.nombre,
                                            descripcion = "",
                                            precio = product.precio,
                                            imageUrl = product.imagen,
                                            cantidadEnCarrito = 1
                                        )
                                    carritoViewModel.addCarrito(productoCarrito)

                                    // Mostrar Snackbar de confirmación
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Añadido: ${product.nombre}",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            )
                        }

                        // 🔹 Mostrar carrito al final si hay productos
                        if (productosCarrito.isNotEmpty()) {
                            item {
                                Divider(Modifier.padding(vertical = 8.dp))
                                Text(
                                    text = "🛒 Carrito (${productosCarrito.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(16.dp)
                                )

                                productosCarrito.forEach { producto ->
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(producto.nombre)
                                        Text(
                                            text = "x${producto.cantidadEnCarrito}",
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                val total = productosCarrito.sumOf {
                                    it.precio * it.cantidadEnCarrito
                                }

                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Total: $${"%.2f".format(total)}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Gracias por tu compra 🛒✨")
                                            }
                                            carritoViewModel.clearCarrito()
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Pagar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryRow(
    category: Category,
    onAddToCart: (Product) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = category.categoria,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            items(category.productos) { product ->
                ProductCard(
                    product = product,
                    onAddToCartClick = onAddToCart,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onAddToCartClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(170.dp)
            .height(210.dp)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .height(110.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(product.imagen),
                    contentDescription = product.nombre,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            Text(
                text = product.nombre,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 2
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${"%.2f".format(product.precio)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                )

                Button(
                    onClick = { onAddToCartClick(product) },
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Filled.AddShoppingCart,
                        contentDescription = "Añadir al carrito",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
