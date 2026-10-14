package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EdmarRonaldoTokColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = DeepBlack,
    primaryContainer = TechPurple,
    onPrimaryContainer = BrightWhite,
    secondary = VibrantRed,
    onSecondary = BrightWhite,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = BrightWhite,
    tertiary = GoldCrown,
    onTertiary = DeepBlack,
    background = DeepBlack,
    onBackground = BrightWhite,
    surface = CinemaBlack,
    onSurface = BrightWhite,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = ElegantGrey,
    outline = GlassBorder,
    outlineVariant = SubtleGrey
)

@Composable
fun EdmarRonaldoTokTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = EdmarRonaldoTokColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    EdmarRonaldoTokTheme(content = content)
}
