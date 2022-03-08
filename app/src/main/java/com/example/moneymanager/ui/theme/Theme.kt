package com.example.moneymanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = BlueCrayola,
    primaryVariant = Gold,
    secondary = Vermilion,
    secondaryVariant = Gray,
    background = RichBlack,
    onBackground = Snow,
)

private val LightColorPalette = lightColors(
    primary = BlueCrayola, // blue money colour
    primaryVariant = Gold,
    secondary = Vermilion, // red money colour
    secondaryVariant = RichBlack, // Text colour
    background = Cultured,
    onBackground = RichBlack,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun MoneyManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}