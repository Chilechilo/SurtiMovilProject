package com.surtiapp.surtimovil.onboarding.model

import com.surtiapp.surtimovil.R

/**
 * Contenido del onboarding
 * Las paginas que tendra el onboarding
 */
object OnboardingContent {
    val pages = listOf(
        OnboardingPageModel(
            imageRes = R.drawable.onb_1,
            title = "Tu tienda siempre llena, sin perder tiempo",
            description = "Desde tu celular, elige los abarrotes que tu negocio necesita. Olvídate de salir y perder tiempo."
        ),
        OnboardingPageModel(
            imageRes = R.drawable.onb_2,
            title = "Pide facil,recibe rapido.",
            description = "Nunca te quedas sin surtido. Más ventas, menos distracciones."
        ),
        OnboardingPageModel(
            imageRes = R.drawable.onb_3,
            title = "Todo en un solo lugar",
            description = "Precios competitivos, entrega directa y catálogo completo. Descarga, pide y sigue vendiendo."
        )
    )
}