package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = StmBlue,
    onPrimary = TextPrimaryLight,
    primaryContainer = StmBlueDark,
    secondary = WarmGold,
    onSecondary = ImmersiveBg,
    secondaryContainer = WarmGoldVariant,
    tertiary = StmGreen,
    background = ImmersiveBg,
    surface = ImmersiveSurface,
    surfaceVariant = ImmersiveCardBg,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = ImmersiveGlassBorder
)

private val LightColorScheme = lightColorScheme(
    primary = StmBlue,
    onPrimary = LightSurface,
    primaryContainer = StmBlueLight,
    secondary = WarmGold,
    onSecondary = ImmersiveBg,
    tertiary = StmGreen,
    background = LightBg,
    surface = LightSurface,
    onBackground = LightPrimary,
    onSurface = LightPrimary,
    onSurfaceVariant = TextMuted,
    outline = ImmersiveGlassBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our high-contrast custom brand palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // Default to dark cyber aesthetic for transit radar
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
