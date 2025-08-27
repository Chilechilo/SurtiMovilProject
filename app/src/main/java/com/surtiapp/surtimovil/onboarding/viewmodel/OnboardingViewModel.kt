package com.surtiapp.surtimovil.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import com.surtiapp.surtimovil.onboarding.model.OnboardingContent
import com.surtiapp.surtimovil.onboarding.model.OnboardingPageModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Aquí se encuntra la logica de como se van a manejar las paginas del onboarding
 */
class OnboardingViewModel : ViewModel() {

    private val _pages = MutableStateFlow(OnboardingContent.pages)
    val pages: StateFlow<List<OnboardingPageModel>> = _pages.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val lastIndex: Int get() = _pages.value.lastIndex

    fun setPage(index: Int) {
        _currentPage.value = index.coerceIn(0, lastIndex)
    }

    fun nextPage() = setPage(_currentPage.value + 1)
    fun prevPage() = setPage(_currentPage.value - 1)

    fun isLastPage(index: Int = _currentPage.value): Boolean = index == lastIndex
}