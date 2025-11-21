package com.surtiapp.surtimovil.addcart.model

/**
 * Clase que representa un producto dentro del carrito de compras.
 * Ya no depende de Firebase, se maneja localmente.
 */
data class CartItem(
    var productId: String = "",
    var productName: String = "",
    var productPrice: Double = 0.0,
    var productImageUrl: String = "",
    var quantity: Int = 0
) {
    // Subtotal calculado localmente (precio × cantidad)
    val subtotal: Double
        get() = productPrice * quantity
}
