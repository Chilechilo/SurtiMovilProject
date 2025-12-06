package com.surtiapp.surtimovil.core.offers.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.surtiapp.surtimovil.homescreen.repository.OffersRepository

class OffersViewModelFactory(
    private val repository: OffersRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OffersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OffersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}