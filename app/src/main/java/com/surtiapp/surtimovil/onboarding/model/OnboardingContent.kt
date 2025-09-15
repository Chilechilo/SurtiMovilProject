package com.surtiapp.surtimovil.onboarding.model

import com.surtiapp.surtimovil.R

object OnboardingContent {
    val pages = listOf(
        OnboardingPageModel(
            imageRes = R.drawable.onb_1,
            titleRes = R.string.onb_title_1,
            descriptionRes = R.string.onb_desc_1
        ),
        OnboardingPageModel(
            imageRes = R.drawable.onb_2,
            titleRes = R.string.onb_title_2,
            descriptionRes = R.string.onb_desc_2
        ),
        OnboardingPageModel(
            imageRes = R.drawable.onb_3,
            titleRes = R.string.onb_title_3,
            descriptionRes = R.string.onb_desc_3
        )
    )
}
