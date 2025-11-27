package com.roadi.budgesfram.kgoed.domain.usecases

import android.util.Log
import com.roadi.budgesfram.kgoed.data.repo.RoadBudgetFarmRepository
import com.roadi.budgesfram.kgoed.data.utils.RoadBudgetFarmPushToken
import com.roadi.budgesfram.kgoed.data.utils.RoadBudgetFarmSystemService
import com.roadi.budgesfram.kgoed.domain.model.RoadBudgetFarmEntity
import com.roadi.budgesfram.kgoed.domain.model.RoadBudgetFarmParam
import com.roadi.budgesfram.kgoed.presentation.app.RoadBudgetFarmApplication

class RoadBudgetFarmGetAllUseCase(
    private val roadBudgetFarmRepository: RoadBudgetFarmRepository,
    private val roadBudgetFarmSystemService: RoadBudgetFarmSystemService,
    private val roadBudgetFarmPushToken: RoadBudgetFarmPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : RoadBudgetFarmEntity?{
        val params = RoadBudgetFarmParam(
            roadBudgetFarmLocale = roadBudgetFarmSystemService.roadBudgetFarmGetLocale(),
            roadBudgetFarmPushToken = roadBudgetFarmPushToken.roadBudgetFarmGetToken(),
            roadBudgetFarmAfId = roadBudgetFarmSystemService.roadBudgetFarmGetAppsflyerId()
        )
        Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "Params for request: $params")
        return roadBudgetFarmRepository.roadBudgetFarmGetClient(params, conversion)
    }



}