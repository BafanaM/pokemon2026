package com.example.pokemonchallenge.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PokemonBlue,
    onPrimary = OnPrimary,
    primaryContainer = PokemonPurple,
    onPrimaryContainer = OnSurface,
    secondary = PokemonYellow,
    onSecondary = OnSecondary,
    secondaryContainer = PokemonOrange,
    onSecondaryContainer = OnSurface,
    tertiary = PokemonGreen,
    onTertiary = OnSurface,
    tertiaryContainer = PokemonPurple,
    onTertiaryContainer = OnSurface,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE1E1E1),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE1E1E1),
    error = Error,
    onError = OnError,
    errorContainer = PokemonRed.copy(alpha = 0.2f),
    onErrorContainer = OnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = OnSurface,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = OnSurface,
    tertiary = PokemonGreen,
    onTertiary = OnSurface,
    tertiaryContainer = PokemonPurple,
    onTertiaryContainer = OnSurface,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    error = Error,
    onError = OnError,
    errorContainer = PokemonRed.copy(alpha = 0.2f),
    onErrorContainer = OnSurface
)

@Composable
fun PokemonsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use our custom Pokemon colors
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