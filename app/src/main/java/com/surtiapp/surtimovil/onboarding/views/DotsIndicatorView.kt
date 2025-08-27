package com.surtiapp.surtimovil.onboarding.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Vista que muestra el indicador actual de la pagina
 */
@Composable
fun DotsIndicatorView(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        repeat(totalDots) { index ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (selected) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
            )
        }
    }
}