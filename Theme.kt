package com.saeid.italyaiculturaltourism.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ItalyGreen = Color(0xFF0B5D4A)
private val ItalyGold = Color(0xFFC08A2B)
private val ItalyRed = Color(0xFF8B2735)
private val Ivory = Color(0xFFFFFBF4)

private val LightColors = lightColorScheme(
    primary = ItalyGreen,
    onPrimary = Color.White,
    secondary = ItalyGold,
    onSecondary = Color.White,
    tertiary = ItalyRed,
    background = Ivory,
    surface = Color.White,
    surfaceVariant = Color(0xFFF1ECE1),
    onSurfaceVariant = Color(0xFF5A554A)
)

@Composable
fun ItalyTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, typography = Typography(), content = content)
}
