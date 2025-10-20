package com.surtiapp.surtimovil.Addcarrito.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

/**
 * Clase que representa un producto dentro del carrito de compras.
 * Diseñada para ser almacenada en Firestore.
 */
data class CartItem(
    @get:PropertyName("product_id") @set:PropertyName("product_id") // Identificador único del producto
    var productId: String = "",

    @get:PropertyName("product_name") @set:PropertyName("product_name") // Nombre del producto
    var productName: String = "",

    @get:PropertyName("product_price") @set:PropertyName("product_price") // Precio unitario
    var productPrice: Double = 0.0,

    @get:PropertyName("product_image_url") @set:PropertyName("product_image_url") // URL de la imagen para mostrar en el carrito
    var productImageUrl: String = "",

    // Cantidad seleccionada por el usuario
    var quantity: Int = 0,

    // El ID único que Firestore le asigna al documento del carrito (no se guarda en el documento, solo en Kotlin)
    @get:Exclude @set:Exclude
    var documentId: String = ""
) {
    // Getter para calcular el subtotal en Kotlin (no se guarda en Firestore)
    @get:Exclude
    val subtotal: Double
        get() = productPrice * quantity
}
