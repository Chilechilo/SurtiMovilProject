package com.surtiapp.surtimovil.homescreen.home.views

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
import com.surtiapp.surtimovil.addcart.viewmodel.CartViewModel
import com.surtiapp.surtimovil.homescreen.home.HomeUiState
import com.surtiapp.surtimovil.homescreen.home.HomeViewModel
import com.surtiapp.surtimovil.homescreen.model.dto.CategoryDto
import com.surtiapp.surtimovil.homescreen.model.dto.ProductDto
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke

@Composable
fun HomeViewProducts(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    cartViewModel: CartViewModel,
    modifier: Modifier = Modifier
) {
    val productosCarrito by cartViewModel.productosEnCarrito.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(text = uiState.error ?: "Error", color = MaterialTheme.colorScheme.error)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(uiState.categorias) { category ->
                            CategoryRow(
                                category = category,
                                productos = uiState.productos.filter { it.category == category.category },
                                onAddToCart = { product ->
                                    val productoCarrito =
                                        com.surtiapp.surtimovil.addcart.model.Producto(
                                            id = "${category.category}_${product.id}",
                                            nombre = product.name,
                                            descripcion = "",
                                            precio = product.price,
                                            imageUrl = product.image,
                                            cantidadEnCarrito = 1
                                        )
                                    cartViewModel.addCarrito(productoCarrito)

                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Añadido: ${product.name}",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryRow(
    category: CategoryDto,
    productos: List<ProductDto>,
    onAddToCart: (ProductDto) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = category.category,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(productos) { product ->
                ProductCard(
                    product = product,
                    onAddToCartClick = onAddToCart
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: ProductDto,
    onAddToCartClick: (ProductDto) -> Unit
) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .height(210.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = product.name,
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )

            Text(
                text = product.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${"%.2f".format(product.price)}",
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = { onAddToCartClick(product) },
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Filled.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
