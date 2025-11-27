package com.roadi.budgesfram.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.roadi.budgesfram.data.preferences.PreferencesManager
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

object ThemeProvider {
    fun getColorScheme(themeName: String): ColorScheme {
        return when (themeName) {
            "Light" -> LightTheme
            "Dark" -> DarkTheme
            "Farm" -> FarmTheme
            else -> FarmTheme
        }
    }
}

private val FarmTheme = lightColorScheme(
    primary = GoldAccent,
    onPrimary = White,
    primaryContainer = GoldAccent,
    onPrimaryContainer = White,
    secondary = OrangeAccent,
    onSecondary = White,
    secondaryContainer = OrangeAccent,
    onSecondaryContainer = White,
    tertiary = GreenPositive,
    onTertiary = White,
    error = RedNegative,
    onError = White,
    errorContainer = RedNegative,
    onErrorContainer = White,
    background = BackgroundLight,
    onBackground = TextNeutral,
    surface = White,
    onSurface = TextNeutral,
    surfaceVariant = White,
    onSurfaceVariant = TextNeutral,
    outline = TextNeutral
)

private val LightTheme = lightColorScheme(
    primary = GoldAccent,
    onPrimary = White,
    secondary = OrangeAccent,
    onSecondary = White,
    tertiary = GreenPositive,
    onTertiary = White,
    error = RedNegative,
    onError = White,
    background = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
    onBackground = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    surface = White,
    onSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE7E0EC),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF49454F)
)

private val DarkTheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = androidx.compose.ui.graphics.Color(0xFF3C3000),
    secondary = OrangeAccent,
    onSecondary = androidx.compose.ui.graphics.Color(0xFF442B08),
    tertiary = GreenPositive,
    onTertiary = androidx.compose.ui.graphics.Color(0xFF00391C),
    error = RedNegative,
    onError = androidx.compose.ui.graphics.Color(0xFF690005),
    background = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
    surface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF49454F),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFCAC4D0)
)
