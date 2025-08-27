package com.surtiapp.surtimovil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary            = Terracotta,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFFFFE1D8),
    onPrimaryContainer = Color(0xFF3F2018),

    secondary          = Amber,
    onSecondary        = Color(0xFF1F1208),
    secondaryContainer = Color(0xFFFFE2C8),
    onSecondaryContainer = Color(0xFF2F1600),

    tertiary           = Olive,
    onTertiary         = Color(0xFF0F1E0D),

    background         = Cream,          // fondo general
    onBackground       = TextDark,
    surface            = Color.White,    // tarjetas/surfaces
    onSurface          = TextDark,

    outline            = OutlineWarm
)

private val DarkColors = darkColorScheme(
    primary            = TerracottaDark,
    onPrimary          = Color(0xFF4C2317),
    secondary          = AmberDark,
    onSecondary        = Color(0xFF3B1D00),
    tertiary           = OliveDark,
    onTertiary         = Color(0xFF0C210F),

    background         = BgDark,
    onBackground       = TextLight,
    surface            = SurfaceDark,
    onSurface          = TextLight,

    outline            = OutlineDark
)

@Composable
fun SurtiMovilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
