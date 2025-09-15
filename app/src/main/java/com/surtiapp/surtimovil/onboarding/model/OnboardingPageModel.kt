package com.surtiapp.surtimovil.onboarding.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Modelo de datos para el onboarding
 */
data class OnboardingPageModel(
    @DrawableRes val imageRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int
)
