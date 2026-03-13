package com.example.encuestaapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val lightScheme = lightColorScheme(
    primary = AppLightPrimary,
    onPrimary = AppLightOnPrimary,
    primaryContainer = AppLightPrimaryContainer,
    onPrimaryContainer = AppLightOnPrimaryContainer,
    secondary = AppLightSecondary,
    onSecondary = AppLightOnSecondary,
    secondaryContainer = AppLightSecondaryContainer,
    onSecondaryContainer = AppLightOnSecondaryContainer,
    tertiary = AppLightTertiary,
    onTertiary = AppLightOnTertiary,
    tertiaryContainer = AppLightTertiaryContainer,
    onTertiaryContainer = AppLightOnTertiaryContainer,
    error = AppLightError,
    onError = AppLightOnError,
    background = AppLightBackground,
    onBackground = AppLightOnBackground,
    surface = AppLightSurface,
    onSurface = AppLightOnSurface,
)

private val darkScheme = darkColorScheme(
    primary = AppDarkPrimary,
    onPrimary = AppDarkOnPrimary,
    primaryContainer = AppDarkPrimaryContainer,
    onPrimaryContainer = AppDarkOnPrimaryContainer,
    secondary = AppDarkSecondary,
    onSecondary = AppDarkOnSecondary,
    secondaryContainer = AppDarkSecondaryContainer,
    onSecondaryContainer = AppDarkOnSecondaryContainer,
    tertiary = AppDarkTertiary,
    onTertiary = AppDarkOnTertiary,
    tertiaryContainer = AppDarkTertiaryContainer,
    onTertiaryContainer = AppDarkOnTertiaryContainer,
    error = AppDarkError,
    onError = AppDarkOnError,
    background = AppDarkBackground,
    onBackground = AppDarkOnBackground,
    surface = AppDarkSurface,
    onSurface = AppDarkOnSurface,
)

@Composable
fun EncuestaAPPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
