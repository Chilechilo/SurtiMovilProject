package com.surtiapp.surtimovil.homescreen.home.login // Ajusta el paquete si es diferente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.surtiapp.surtimovil.core.homescreen.repository.HomeRepository
import com.surtiapp.surtimovil.homescreen.home.HomeViewModel
import com.surtiapp.surtimovil.homescreen.repository.CartRepository

class HomeViewModelFactory(
    private val repo: HomeRepository,
    private val cartRepository: CartRepository // Recibe CartRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            // ✅ CORREGIDO: Pasa AMBOS repositorios al HomeViewModel
            return HomeViewModel(
                homeRepository = repo,
                cartRepository = cartRepository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}