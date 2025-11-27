package com.roadi.budgesfram.kgoed.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadi.budgesfram.kgoed.data.shar.RoadBudgetFarmSharedPreference
import com.roadi.budgesfram.kgoed.data.utils.RoadBudgetFarmSystemService
import com.roadi.budgesfram.kgoed.domain.usecases.RoadBudgetFarmGetAllUseCase
import com.roadi.budgesfram.kgoed.presentation.app.RoadBudgetFarmAppsFlyerState
import com.roadi.budgesfram.kgoed.presentation.app.RoadBudgetFarmApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoadBudgetFarmLoadViewModel(
    private val roadBudgetFarmGetAllUseCase: RoadBudgetFarmGetAllUseCase,
    private val roadBudgetFarmSharedPreference: RoadBudgetFarmSharedPreference,
    private val roadBudgetFarmSystemService: RoadBudgetFarmSystemService
) : ViewModel() {

    private val _roadBudgetFarmHomeScreenState: MutableStateFlow<RoadBudgetFarmHomeScreenState> =
        MutableStateFlow(RoadBudgetFarmHomeScreenState.RoadBudgetFarmLoading)
    val roadBudgetFarmHomeScreenState = _roadBudgetFarmHomeScreenState.asStateFlow()

    private var roadBudgetFarmGetApps = false


    init {
        viewModelScope.launch {
            when (roadBudgetFarmSharedPreference.roadBudgetFarmAppState) {
                0 -> {
                    if (roadBudgetFarmSystemService.roadBudgetFarmIsOnline()) {
                        RoadBudgetFarmApplication.roadBudgetFarmConversionFlow.collect {
                            when(it) {
                                RoadBudgetFarmAppsFlyerState.RoadBudgetFarmDefault -> {}
                                RoadBudgetFarmAppsFlyerState.RoadBudgetFarmError -> {
                                    roadBudgetFarmSharedPreference.roadBudgetFarmAppState = 2
                                    _roadBudgetFarmHomeScreenState.value =
                                        RoadBudgetFarmHomeScreenState.RoadBudgetFarmError
                                    roadBudgetFarmGetApps = true
                                }
                                is RoadBudgetFarmAppsFlyerState.RoadBudgetFarmSuccess -> {
                                    if (!roadBudgetFarmGetApps) {
                                        roadBudgetFarmGetData(it.roadBudgetFarmData)
                                        roadBudgetFarmGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _roadBudgetFarmHomeScreenState.value =
                            RoadBudgetFarmHomeScreenState.RoadBudgetFarmNotInternet
                    }
                }
                1 -> {
                    if (roadBudgetFarmSystemService.roadBudgetFarmIsOnline()) {
                        if (RoadBudgetFarmApplication.ROAD_BUDGET_FARM_FB_LI != null) {
                            _roadBudgetFarmHomeScreenState.value =
                                RoadBudgetFarmHomeScreenState.RoadBudgetFarmSuccess(
                                    RoadBudgetFarmApplication.ROAD_BUDGET_FARM_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > roadBudgetFarmSharedPreference.roadBudgetFarmExpired) {
                            Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "Current time more then expired, repeat request")
                            RoadBudgetFarmApplication.roadBudgetFarmConversionFlow.collect {
                                when(it) {
                                    RoadBudgetFarmAppsFlyerState.RoadBudgetFarmDefault -> {}
                                    RoadBudgetFarmAppsFlyerState.RoadBudgetFarmError -> {
                                        _roadBudgetFarmHomeScreenState.value =
                                            RoadBudgetFarmHomeScreenState.RoadBudgetFarmSuccess(
                                                roadBudgetFarmSharedPreference.roadBudgetFarmSavedUrl
                                            )
                                        roadBudgetFarmGetApps = true
                                    }
                                    is RoadBudgetFarmAppsFlyerState.RoadBudgetFarmSuccess -> {
                                        if (!roadBudgetFarmGetApps) {
                                            roadBudgetFarmGetData(it.roadBudgetFarmData)
                                            roadBudgetFarmGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "Current time less then expired, use saved url")
                            _roadBudgetFarmHomeScreenState.value =
                                RoadBudgetFarmHomeScreenState.RoadBudgetFarmSuccess(
                                    roadBudgetFarmSharedPreference.roadBudgetFarmSavedUrl
                                )
                        }
                    } else {
                        _roadBudgetFarmHomeScreenState.value =
                            RoadBudgetFarmHomeScreenState.RoadBudgetFarmNotInternet
                    }
                }
                2 -> {
                    _roadBudgetFarmHomeScreenState.value =
                        RoadBudgetFarmHomeScreenState.RoadBudgetFarmError
                }
            }
        }
    }


    private suspend fun roadBudgetFarmGetData(conversation: MutableMap<String, Any>?) {
        val roadBudgetFarmData = roadBudgetFarmGetAllUseCase.invoke(conversation)
        if (roadBudgetFarmSharedPreference.roadBudgetFarmAppState == 0) {
            if (roadBudgetFarmData == null) {
                roadBudgetFarmSharedPreference.roadBudgetFarmAppState = 2
                _roadBudgetFarmHomeScreenState.value =
                    RoadBudgetFarmHomeScreenState.RoadBudgetFarmError
            } else {
                roadBudgetFarmSharedPreference.roadBudgetFarmAppState = 1
                roadBudgetFarmSharedPreference.apply {
                    roadBudgetFarmExpired = roadBudgetFarmData.roadBudgetFarmExpires
                    roadBudgetFarmSavedUrl = roadBudgetFarmData.roadBudgetFarmUrl
                }
                _roadBudgetFarmHomeScreenState.value =
                    RoadBudgetFarmHomeScreenState.RoadBudgetFarmSuccess(roadBudgetFarmData.roadBudgetFarmUrl)
            }
        } else  {
            if (roadBudgetFarmData == null) {
                _roadBudgetFarmHomeScreenState.value =
                    RoadBudgetFarmHomeScreenState.RoadBudgetFarmSuccess(roadBudgetFarmSharedPreference.roadBudgetFarmSavedUrl)
            } else {
                roadBudgetFarmSharedPreference.apply {
                    roadBudgetFarmExpired = roadBudgetFarmData.roadBudgetFarmExpires
                    roadBudgetFarmSavedUrl = roadBudgetFarmData.roadBudgetFarmUrl
                }
                _roadBudgetFarmHomeScreenState.value =
                    RoadBudgetFarmHomeScreenState.RoadBudgetFarmSuccess(roadBudgetFarmData.roadBudgetFarmUrl)
            }
        }
    }


    sealed class RoadBudgetFarmHomeScreenState {
        data object RoadBudgetFarmLoading : RoadBudgetFarmHomeScreenState()
        data object RoadBudgetFarmError : RoadBudgetFarmHomeScreenState()
        data class RoadBudgetFarmSuccess(val data: String) : RoadBudgetFarmHomeScreenState()
        data object RoadBudgetFarmNotInternet: RoadBudgetFarmHomeScreenState()
    }
}