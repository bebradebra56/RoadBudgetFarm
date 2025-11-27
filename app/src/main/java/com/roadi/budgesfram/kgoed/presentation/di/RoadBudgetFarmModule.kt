package com.roadi.budgesfram.kgoed.presentation.di

import com.roadi.budgesfram.kgoed.data.repo.RoadBudgetFarmRepository
import com.roadi.budgesfram.kgoed.data.shar.RoadBudgetFarmSharedPreference
import com.roadi.budgesfram.kgoed.data.utils.RoadBudgetFarmPushToken
import com.roadi.budgesfram.kgoed.data.utils.RoadBudgetFarmSystemService
import com.roadi.budgesfram.kgoed.domain.usecases.RoadBudgetFarmGetAllUseCase
import com.roadi.budgesfram.kgoed.presentation.pushhandler.RoadBudgetFarmPushHandler
import com.roadi.budgesfram.kgoed.presentation.ui.load.RoadBudgetFarmLoadViewModel
import com.roadi.budgesfram.kgoed.presentation.ui.view.RoadBudgetFarmViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val roadBudgetFarmModule = module {
    factory {
        RoadBudgetFarmPushHandler()
    }
    single {
        RoadBudgetFarmRepository()
    }
    single {
        RoadBudgetFarmSharedPreference(get())
    }
    factory {
        RoadBudgetFarmPushToken()
    }
    factory {
        RoadBudgetFarmSystemService(get())
    }
    factory {
        RoadBudgetFarmGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        RoadBudgetFarmViFun(get())
    }
    viewModel {
        RoadBudgetFarmLoadViewModel(get(), get(), get())
    }
}