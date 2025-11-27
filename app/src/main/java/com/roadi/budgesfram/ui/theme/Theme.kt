package com.roadi.budgesfram.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.roadi.budgesfram.data.preferences.PreferencesManager
import org.koin.compose.koinInject

@Composable
fun RoadBudgetFarmTheme(
    content: @Composable () -> Unit
) {
    val preferencesManager: PreferencesManager = koinInject()
    val theme by preferencesManager.theme.collectAsState()
    
    val colorScheme = ThemeProvider.getColorScheme(theme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}