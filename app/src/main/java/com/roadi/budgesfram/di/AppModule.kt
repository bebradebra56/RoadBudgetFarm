package com.roadi.budgesfram.di

import androidx.room.Room
import com.roadi.budgesfram.data.database.AppDatabase
import com.roadi.budgesfram.data.database.DatabaseInitializer
import com.roadi.budgesfram.data.repository.CategoryRepository
import com.roadi.budgesfram.data.repository.TransactionRepository
import com.roadi.budgesfram.ui.viewmodels.AddTransactionViewModel
import com.roadi.budgesfram.ui.viewmodels.CategoriesViewModel
import com.roadi.budgesfram.ui.viewmodels.DashboardViewModel
import com.roadi.budgesfram.ui.viewmodels.HistoryViewModel
import com.roadi.budgesfram.ui.viewmodels.StatisticsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Database
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    // DAOs
    single { get<AppDatabase>().transactionDao() }
    single { get<AppDatabase>().categoryDao() }

    // Repositories
    single { TransactionRepository(get()) }
    single { CategoryRepository(get()) }

    // Preferences
    single { com.roadi.budgesfram.data.preferences.PreferencesManager(get()) }

    // Database Initializer
    single { DatabaseInitializer(get()) }

    // ViewModels
    viewModel { DashboardViewModel(get()) }
    viewModel { AddTransactionViewModel(get(), get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { StatisticsViewModel(get(), get()) }
    viewModel { CategoriesViewModel(get()) }
    viewModel { com.roadi.budgesfram.ui.viewmodels.SettingsViewModel(get(), get(), get(), get()) }
}
