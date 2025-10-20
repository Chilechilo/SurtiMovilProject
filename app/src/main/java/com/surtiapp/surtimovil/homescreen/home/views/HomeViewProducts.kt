package com.surtiapp.surtimovil.home.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.surtiapp.surtimovil.homescreen.home.HomeUiState
import com.surtiapp.surtimovil.homescreen.model.dto.Category
import com.surtiapp.surtimovil.homescreen.model.dto.Product
// ------------------------------------------
// NUEVAS IMPORTACIONES PARA EL CARRITO Y UI
import com.surtiapp.surtimovil.homescreen.home.HomeViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.foundation.BorderStroke
// ------------------------------------------


@Composable
fun HomeViewProducts(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    // ESTADO AÑADIDO: Mantiene el producto seleccionado para mostrar el modal
    var selectedProduct: Product? by remember { mutableStateOf(null) }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error, color = MaterialTheme.colorScheme.error)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // Pasar el estado de selección a CategoryRow
                    items(uiState.categorias) { category ->
                        CategoryRow(
                            category = category,
                            onProductClick = { product -> selectedProduct = product } // Se define el callback
                        )
                    }
                }
            }
        }
    }

    // ******************************************************
    // ACTIVACIÓN DEL MODAL (BOTTOM SHEET) - CONEXIÓN FINAL
    // ******************************************************
    selectedProduct?.let { product ->
        AddToCartBottomSheet(
            product = product,
            onDismiss = { selectedProduct = null },
            // 🎯 CORRECCIÓN CLAVE: La lambda del modal recibe DOS argumentos,
            // pero solo usamos 'productToAdd' para llamar a la función del VM.
            onAddToCart = { productToAdd, quantity ->

                // Antes: viewModel.addToCart(selectedProduct, quantity) <--- ESTO ES DEMASIADOS ARGUMENTOS

                // AHORA: Llama a la función del ViewModel que SOLO acepta el Producto
                viewModel.addToCart(productToAdd) // ✅ ¡Solo un argumento!

                selectedProduct = null // Cierra el modal después de añadir
            }
        )
    }
}

// ------------------------------------------------------------
// Se modifica la firma de CategoryRow para recibir la función de click
// ------------------------------------------------------------
@Composable
fun CategoryRow(category: Category, onProductClick: (Product) -> Unit) {
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
                    onAddToCartClick = onProductClick, // Se pasa la acción
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            }
        }
    }
}

// ------------------------------------------------------------
// Se modifica la firma de ProductCard para usar el callback del click
// ------------------------------------------------------------
@Composable
fun ProductCard(
    product: Product,
    onAddToCartClick: (Product) -> Unit, // Nuevo callback para el click
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
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 2
            )

            // Fila de Precio y Botón de Carrito
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Precio (asumo que 'precio' es un Double en tu Product DTO)
                Text(
                    text = "$${"%.2f".format(product.precio)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                )

                // Botón de Añadir al Carrito
                Button(
                    // Llama al callback para mostrar el BottomSheet (a través de onProductClick en HomeViewProducts)
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
