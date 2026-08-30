package com.sherryyuan.aphora.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = NavyAccent,
    onPrimary = OffWhiteSurface,
    secondary = TaupeSecondary,
    onSecondary = OffWhiteSurface,
    background = CreamBackground,
    onBackground = DarkCharcoal,
    surface = OffWhiteSurface,
    surfaceVariant = OffWhiteSurface,
    onSurface = DarkCharcoal,
    onSurfaceVariant = SubtleCharcoal,
)

private val DarkColorScheme = darkColorScheme(
    primary = LightNavyAccent,
    onPrimary = NavyAccent,
    secondary = LightTaupe,
    onSecondary = DarkCharcoal,
    background = DarkGreyBackground,
    onBackground = SoftCreamText,
    surface = MutedBlackSurface,
    surfaceVariant = MutedBlackSurface,
    onSurface = SoftCreamText,
    onSurfaceVariant = LightTaupe,
)

@Composable
fun AphoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Prefer custom palette over dynamic color
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
