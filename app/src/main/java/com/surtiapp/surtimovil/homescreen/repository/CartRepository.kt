package com.surtiapp.surtimovil.homescreen.repository

import com.surtiapp.surtimovil.addcart.model.CartItem

object CartRepository {
    private val cartItems = mutableListOf<CartItem>()

    fun addItemToCart(userId: String, item: CartItem): Result<Unit> {
        // Simplemente añade el producto localmente
        val existing = cartItems.find { it.productId == item.productId }
        if (existing != null) {
            existing.quantity += item.quantity
        } else {
            cartItems.add(item)
        }
        return Result.success(Unit)
    }

    fun getCartItems(): List<CartItem> = cartItems

    fun clearCart() {
        cartItems.clear()
    }
}
