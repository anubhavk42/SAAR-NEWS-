package com.example.ui.theme

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryAccent,
    onPrimary = DarkSurface,
    secondary = DarkHighlightAccent,
    onSecondary = DarkBackground,
    tertiary = DarkHighlightAccent,
    background = DarkBackground,
    onBackground = DarkPrimaryText,
    surface = DarkSurface,
    onSurface = DarkPrimaryText,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkSecondaryText,
    outline = DarkBorder,
    error = DarkError
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimaryAccent,
    onPrimary = LightSurface,
    secondary = LightHighlightAccent,
    onSecondary = LightBackground,
    tertiary = LightHighlightAccent,
    background = LightBackground,
    onBackground = LightPrimaryText,
    surface = LightSurface,
    onSurface = LightPrimaryText,
    surfaceVariant = LightSurface,
    onSurfaceVariant = LightSecondaryText,
    outline = LightBorder,
    error = LightError
)

private val SepiaColorScheme = lightColorScheme(
    primary = SepiaPrimaryAccent,
    onPrimary = SepiaSurface,
    secondary = SepiaHighlightAccent,
    onSecondary = SepiaBackground,
    tertiary = SepiaHighlightAccent,
    background = SepiaBackground,
    onBackground = SepiaPrimaryText,
    surface = SepiaSurface,
    onSurface = SepiaPrimaryText,
    surfaceVariant = SepiaSurface,
    onSurfaceVariant = SepiaSecondaryText,
    outline = SepiaBorder,
    error = SepiaError
)

private val CharcoalColorScheme = darkColorScheme(
    primary = CharcoalPrimaryAccent,
    onPrimary = CharcoalSurface,
    secondary = CharcoalHighlightAccent,
    onSecondary = CharcoalBackground,
    tertiary = CharcoalHighlightAccent,
    background = CharcoalBackground,
    onBackground = CharcoalPrimaryText,
    surface = CharcoalSurface,
    onSurface = CharcoalPrimaryText,
    surfaceVariant = CharcoalSurface,
    onSurfaceVariant = CharcoalSecondaryText,
    outline = CharcoalBorder,
    error = CharcoalError
)

@Composable
fun DigestTheme(
    themeProfile: String = "Light",
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeProfile) {
        "Dark" -> DarkColorScheme
        "Sepia" -> SepiaColorScheme
        "Charcoal" -> CharcoalColorScheme
        else -> LightColorScheme
    }
    val isDark = themeProfile == "Dark" || themeProfile == "Charcoal"
    val context = LocalContext.current
    val view = LocalView.current
 
    SideEffect {
        val activity = context as? ComponentActivity
        activity?.enableEdgeToEdge(
            statusBarStyle = if (isDark) {
                SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            } else {
                SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT
                )
            },
            navigationBarStyle = if (isDark) {
                SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            } else {
                SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT
                )
            }
        )
 
        val window = (view.context as? Activity)?.window
        if (window != null) {
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !isDark
                isAppearanceLightNavigationBars = !isDark
            }
        }
    }
 
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
