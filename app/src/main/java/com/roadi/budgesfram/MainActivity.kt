package com.roadi.budgesfram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.roadi.budgesfram.ui.navigation.AppNavigator
import com.roadi.budgesfram.ui.theme.RoadBudgetFarmTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoadBudgetFarmApp()
        }
    }
}

@Composable
fun RoadBudgetFarmApp() {
    RoadBudgetFarmTheme {
        AppNavigator()
    }
}

@Preview(showBackground = true)
@Composable
fun RoadBudgetFarmAppPreview() {
    RoadBudgetFarmApp()
}