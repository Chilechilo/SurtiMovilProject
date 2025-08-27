package com.surtiapp.surtimovil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary            = PurplePrimary,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFFEDE7FF),
    onPrimaryContainer = Color(0xFF2C1141),

    secondary          = PeachSecondary,
    // buen contraste sobre peach
    onSecondary        = Color(0xFF1F1400),

    tertiary           = TealTertiary,
    onTertiary         = Color.White,

    background         = BgLight,
    onBackground       = OnTextLight,

    surface            = SurfaceLight,
    onSurface          = OnTextLight,

    outline            = OutlineLight,
)

private val DarkColors = darkColorScheme(
    primary            = PurplePrimaryDark,
    onPrimary          = Color(0xFF221338),

    secondary          = PeachSecondaryDark,
    onSecondary        = Color(0xFF2B1300),

    tertiary           = TealTertiaryDark,
    onTertiary         = Color(0xFF002016),

    background         = BgDark,
    onBackground       = OnTextDark,

    surface            = SurfaceDark,
    onSurface          = OnTextDark,

    outline            = OutlineDark,
)

@Composable
fun SurtiMovilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography, // deja tus tipografías actuales
        content = content
    )
}