package com.surtiapp.surtimovil.addcart.views
// o donde lo tengas, asegúrate de que solo tienes UNA declaración de package

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.surtiapp.surtimovil.addcart.model.Producto // Usa el modelo Producto real

/**
 * Muestra la información de un producto y el botón para añadirlo al carrito.
 * @param producto Los datos del producto a mostrar.
 * @param onAddToCart Función que se llama cuando se presiona el botón, ahora recibe el Producto.
 */
@Composable
fun ProductCard(
    producto: Producto,
    // ✅ CORRECCIÓN CLAVE: onAddToCart AHORA espera recibir el Producto
    onAddToCart: (producto: Producto) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Nombre del Producto
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleLarge
            )
            // ... (otros campos)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Precio
                Text(
                    text = "$${"%.2f".format(producto.precio)}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                // Botón Añadir al Carrito
                // ✅ CORRECCIÓN: Llama al callback pasándole el 'producto' actual
                Button(onClick = { onAddToCart(producto) }) {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        contentDescription = "Añadir al carrito",
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("Añadir")
                }
            }

            // Muestra la cantidad actual si ya está en el carrito
            if (producto.cantidadEnCarrito > 0) {
                Text(
                    text = "En carrito: ${producto.cantidadEnCarrito} unidad(es)",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}