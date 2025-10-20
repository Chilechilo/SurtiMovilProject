package com.surtiapp.surtimovil.Addcarrito.viewmodel

import androidx.lifecycle.ViewModel
import com.surtiapp.surtimovil.Addcarrito.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CarritoViewModel : ViewModel() {

    private val _productosEnCarrito = MutableStateFlow<List<Producto>>(emptyList())
    val productosEnCarrito: StateFlow<List<Producto>> = _productosEnCarrito

    fun addCarrito(productoNuevo: Producto) {
        _productosEnCarrito.update { productosActuales ->
            val productoId = productoNuevo.id.toString() // 🔹 aseguramos String uniforme
            val productoExistente = productosActuales.find { it.id == productoId }

            if (productoExistente != null) {
                productosActuales.map {
                    if (it.id == productoId) {
                        it.copy(cantidadEnCarrito = it.cantidadEnCarrito + 1)
                    } else it
                }
            } else {
                productosActuales + productoNuevo.copy(id = productoId)
            }
        }
    }

    fun removeFromCart(producto: Producto) {
        _productosEnCarrito.update { productosActuales ->
            val productoId = producto.id.toString()
            val existente = productosActuales.find { it.id == productoId }

            if (existente != null && existente.cantidadEnCarrito > 1) {
                productosActuales.map {
                    if (it.id == productoId) {
                        it.copy(cantidadEnCarrito = it.cantidadEnCarrito - 1)
                    } else it
                }
            } else {
                productosActuales.filterNot { it.id == productoId }
            }
        }
    }

    fun clearCarrito() {
        _productosEnCarrito.value = emptyList()
    }

    fun getTotalItems(): Int = _productosEnCarrito.value.sumOf { it.cantidadEnCarrito }
}
