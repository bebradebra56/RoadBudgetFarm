package com.roadi.budgesfram.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.roadi.budgesfram.data.models.TransactionType
import com.roadi.budgesfram.ui.screens.addtransaction.AddTransactionScreen
import com.roadi.budgesfram.ui.screens.dashboard.DashboardScreen

@Composable
fun AppNavigator(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD
    ) {
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onNavigateToAddIncome = {
                    navController.navigate("${Routes.ADD_TRANSACTION}/${TransactionType.INCOME.name}")
                },
                onNavigateToAddExpense = {
                    navController.navigate("${Routes.ADD_TRANSACTION}/${TransactionType.EXPENSE.name}")
                },
                onNavigateToHistory = {
                    navController.navigate(Routes.HISTORY)
                },
                onNavigateToStatistics = {
                    navController.navigate(Routes.STATISTICS)
                },
                onNavigateToCategories = {
                    navController.navigate(Routes.CATEGORIES)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(
            route = "${Routes.ADD_TRANSACTION}/{transactionType}",
            arguments = listOf(
                navArgument("transactionType") {
                    type = NavType.StringType
                    defaultValue = TransactionType.EXPENSE.name
                }
            )
        ) { backStackEntry ->
            val transactionTypeString = backStackEntry.arguments?.getString("transactionType")
                ?: TransactionType.EXPENSE.name
            val transactionType = try {
                TransactionType.valueOf(transactionTypeString)
            } catch (e: IllegalArgumentException) {
                TransactionType.EXPENSE
            }

            AddTransactionScreen(
                transactionType = transactionType,
                onNavigateBack = { navController.popBackStack() },
                onTransactionSaved = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.HISTORY) {
            com.roadi.budgesfram.ui.screens.history.HistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.STATISTICS) {
            com.roadi.budgesfram.ui.screens.statistics.StatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CATEGORIES) {
            com.roadi.budgesfram.ui.screens.categories.CategoriesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            com.roadi.budgesfram.ui.screens.settings.SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
