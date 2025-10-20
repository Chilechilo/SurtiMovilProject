package com.surtiapp.surtimovil.Addcarrito.viewmodel

import androidx.lifecycle.ViewModel
import com.surtiapp.surtimovil.Addcarrito.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel que gestiona el estado y la lógica del carrito de compras.
 * Utiliza StateFlow para proveer reactividad a Jetpack Compose.
 */
class CarritoViewModel : ViewModel() {

    // Estado mutable privado del carrito. Se inicializa con una lista vacía para
    // prevenir NullPointerExceptions.
    private val _productosEnCarrito = MutableStateFlow<List<Producto>>(emptyList())

    // Versión de solo lectura del estado del carrito para que la UI lo consuma.
    val productosEnCarrito: StateFlow<List<Producto>> = _productosEnCarrito

    /**
     * Agrega un producto al carrito o incrementa su cantidad si ya existe.
     * * @param productoNuevo El producto a añadir.
     */
    fun addCarrito(productoNuevo: Producto) {
        // Usamos update para garantizar una actualización segura del estado
        _productosEnCarrito.update { productosActuales ->

            // 1. Buscamos si el producto (por ID) ya está en la lista actual
            val productoExistente = productosActuales.find { it.id == productoNuevo.id }

            if (productoExistente != null) {
                // 2. Si el producto ya existe:
                //    Mapeamos la lista para actualizar solo la cantidad de ese producto.
                productosActuales.map {
                    if (it.id == productoNuevo.id) {
                        // Creamos una COPIA inmutable con la cantidad incrementada en 1
                        it.copy(cantidadEnCarrito = it.cantidadEnCarrito + 1)
                    } else {
                        it // Devolvemos los demás productos sin cambios
                    }
                }
            } else {
                // 3. Si es un producto nuevo:
                //    Lo agregamos a la lista actual con una cantidad inicial de 1.
                productosActuales + productoNuevo.copy(cantidadEnCarrito = 1)
            }
        }
    }

    /**
     * Calcula la suma total de unidades de productos en el carrito.
     * @return El número total de unidades.
     */
    fun getTotalItems(): Int {
        return _productosEnCarrito.value.sumOf { it.cantidadEnCarrito }
    }

    // TODO: Puedes añadir funciones aquí como 'removerProducto', 'cambiarCantidad', etc.
}
