package com.surtiapp.surtimovil.home.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.surtiapp.surtimovil.homescreen.model.dto.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToCartBottomSheet(
    product: Product,
    onDismiss: () -> Unit,
    onAddToCart: (Product, Int) -> Unit
) {
    // State to hold the selected quantity
    var quantity by remember { mutableIntStateOf(1) }

    // State to manage the BottomSheet (for consistency, though ModalBottomSheet takes care of it)
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Drag Handle
                BottomSheetDefaults.DragHandle()
                // Close button for better UX
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Cerrar",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { onDismiss() }
                    )
                }
            }
        },
        // Apply custom color to match design
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Product Information
            ProductDetailsRow(product = product)

            Spacer(modifier = Modifier.height(24.dp))

            // Quantity Selector
            QuantitySelector(
                quantity = quantity,
                onQuantityChange = { newQuantity ->
                    if (newQuantity >= 1) {
                        quantity = newQuantity
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Total Price Calculation
            val subtotal = product.precio * quantity
            Text(
                text = "Subtotal: $${"%.2f".format(subtotal)}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Add to Cart Button
            Button(
                onClick = { onAddToCart(product, quantity) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = quantity > 0
            ) {
                Text("Agregar al Carrito ($quantity)")
            }
        }
    }
}

@Composable
fun ProductDetailsRow(product: Product) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product Image
        Image(
            painter = rememberAsyncImagePainter(product.imagen),
            contentDescription = product.nombre,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            // Product Name
            Text(
                text = product.nombre,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )
            // Product Price
            Text(
                text = "$${"%.2f".format(product.precio)} c/u",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFEFEFEF)) // Fondo ligero para el selector
            .padding(8.dp)
    ) {
        // Decrease Button
        IconButton(
            onClick = { onQuantityChange(quantity - 1) },
            enabled = quantity > 1,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Filled.Remove, contentDescription = "Disminuir cantidad")
        }

        // Quantity Display
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.width(40.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Increase Button
        IconButton(
            onClick = { onQuantityChange(quantity + 1) },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Aumentar cantidad")
        }
    }
}
