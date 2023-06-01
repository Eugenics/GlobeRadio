package com.eugenics.freeradio.ui.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import materialcontentlibrary.scheme.Scheme

fun Scheme.toDarkColorScheme(): ColorScheme = darkColorScheme(
    primary = Color(primary),
    onPrimary = Color(onPrimary),
    primaryContainer = Color(primaryContainer),
    onPrimaryContainer = Color(onPrimaryContainer),
    secondary = Color(secondary),
    onSecondary = Color(onSecondary),
    secondaryContainer = Color(secondaryContainer),
    onSecondaryContainer = Color(onSecondaryContainer),
    tertiary = Color(tertiary),
    onTertiary = Color(onTertiary),
    tertiaryContainer = Color(tertiaryContainer),
    onTertiaryContainer = Color(onTertiaryContainer),
    error = Color(error),
    errorContainer = Color(errorContainer),
    onError = Color(onError),
    onErrorContainer = Color(onErrorContainer),
    background = Color(background),
    onBackground = Color(onBackground),
    surface = Color(surface),
    onSurface = Color(onSurface),
    surfaceVariant = Color(surfaceVariant),
    onSurfaceVariant = Color(onSurfaceVariant),
    outline = Color(outline),
    inverseOnSurface = Color(inverseOnSurface),
    inverseSurface = Color(inverseSurface),
    inversePrimary = Color(inversePrimary),
    outlineVariant = Color(outlineVariant),
    scrim = Color(scrim),
)

fun Scheme.toLightColorScheme(): ColorScheme = lightColorScheme(
    primary = Color(primary),
    onPrimary = Color(onPrimary),
    primaryContainer = Color(primaryContainer),
    onPrimaryContainer = Color(onPrimaryContainer),
    secondary = Color(secondary),
    onSecondary = Color(onSecondary),
    secondaryContainer = Color(secondaryContainer),
    onSecondaryContainer = Color(onSecondaryContainer),
    tertiary = Color(tertiary),
    onTertiary = Color(onTertiary),
    tertiaryContainer = Color(tertiaryContainer),
    onTertiaryContainer = Color(onTertiaryContainer),
    error = Color(error),
    errorContainer = Color(errorContainer),
    onError = Color(onError),
    onErrorContainer = Color(onErrorContainer),
    background = Color(background),
    onBackground = Color(onBackground),
    surface = Color(surface),
    onSurface = Color(onSurface),
    surfaceVariant = Color(surfaceVariant),
    onSurfaceVariant = Color(onSurfaceVariant),
    outline = Color(outline),
    inverseOnSurface = Color(inverseOnSurface),
    inverseSurface = Color(inverseSurface),
    inversePrimary = Color(inversePrimary),
    outlineVariant = Color(outlineVariant),
    scrim = Color(scrim),
)

@Composable
fun ContentDynamicTheme(
    isDarkColorsScheme: Boolean,
    color: Color,
    isSystemTheme: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val argb = color.toArgb()
    val colorScheme = if (isSystemTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isDarkColorsScheme)
            dynamicDarkColorScheme(context)
        else
            dynamicLightColorScheme(context)
    } else {
        if (isDarkColorsScheme) {
            Scheme.darkContent(argb).toDarkColorScheme()
        } else {
            Scheme.lightContent(argb).toLightColorScheme()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
        typography = AppTypography,
        shapes = AppBarShape
    )
}