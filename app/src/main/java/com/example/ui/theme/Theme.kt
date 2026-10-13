package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BinanceGold,
    onPrimary = NavyDark,
    primaryContainer = SignalHoldBg,
    onPrimaryContainer = BinanceGoldLight,
    secondary = SignalBuy,
    onSecondary = NavyDark,
    secondaryContainer = SignalBuyBg,
    onSecondaryContainer = SignalBuy,
    tertiary = AccentCyan,
    onTertiary = NavyDark,
    tertiaryContainer = Color(0xFF003847),
    onTertiaryContainer = AccentCyan,
    background = NavyDark,
    onBackground = TextPrimaryDark,
    surface = NavyCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = NavyCardElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = NavyBorder,
    outlineVariant = NavyBorderLight,
    error = SignalSell,
    onError = Color.White,
    errorContainer = SignalSellBg,
    onErrorContainer = SignalSell
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD97706),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF92400E),
    secondary = LightBuy,
    onSecondary = Color.White,
    secondaryContainer = LightBuyBg,
    onSecondaryContainer = LightBuy,
    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0F2FE),
    onTertiaryContainer = Color(0xFF0369A1),
    background = LightBg,
    onBackground = TextPrimaryLight,
    surface = LightCard,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightCardElevated,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder,
    outlineVariant = Color(0xFFCBD5E1),
    error = LightSell,
    onError = Color.White,
    errorContainer = LightSellBg,
    onErrorContainer = LightSell
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek Pro Trading obsidian mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
