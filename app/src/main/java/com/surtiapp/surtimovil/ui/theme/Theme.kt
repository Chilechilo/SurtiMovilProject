package com.surtiapp.surtimovil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// NOTA: Se asume que TextDark ya está definido en otro archivo como un color oscuro (ej. Negro)

private val LightColors = lightColorScheme(
    primary            = Terracotta,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF3F2018),

    secondary          = Amber,
    onSecondary        = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF2F1600),

    tertiary           = Olive,
    onTertiary         = Color(0xFFFFFFFF),

    // ********** CAMBIO CLAVE AQUÍ **********
    background         = Color.White,    // Fondo general cambiado de 'Cream' a BLANCO PURO
    onBackground       = TextDark,
    surface            = Color.White,    // Tarjetas/Superficies ya era Color.White
    onSurface          = TextDark,
    // ****************************************

    outline            = OutlineWarm
)

private val DarkColors = darkColorScheme(
    primary            = TerracottaDark,
    onPrimary          = Color(0xFFFFFFFF),
    secondary          = AmberDark,
    onSecondary        = Color(0xFFFFFFFF),
    tertiary           = OliveDark,
    onTertiary         = Color(0xFFFFFFFF),

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