package com.surtiapp.surtimovil.homescreen.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.surtiapp.surtimovil.Addcarrito.model.CartItem
import kotlinx.coroutines.tasks.await
import java.lang.Void

/**
 * Repositorio dedicado a manejar las operaciones del carrito de compras en Firestore.
 */
class CartRepository(private val firestore: FirebaseFirestore) {

    private fun getUserCartCollection(userId: String) =
        firestore.collection("users").document(userId).collection("cart")

    suspend fun addItemToCart(userId: String, cartItem: CartItem): Result<Void?> {

        // 💥 CORRECCIÓN FINAL: Usamos la propiedad .length (que siempre existe en el String)
        if (cartItem.productId.length == 0) {
            return Result.failure(IllegalArgumentException("El ID del producto no puede ser vacío."))
        }

        return try {
            getUserCartCollection(userId)
                .add(cartItem)
                .await()

            Result.success(null as Void?)

        } catch (e: Exception) {
            Result.failure(Exception("Error al guardar el ítem en Firestore: ${e.localizedMessage}"))
        }
    }
}