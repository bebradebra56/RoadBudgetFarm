package com.roadi.budgesfram.kgoed.domain.model

import com.google.gson.annotations.SerializedName


private const val ROAD_BUDGET_FARM_A = "com.roadi.budgesfram"
private const val ROAD_BUDGET_FARM_B = "roadbudgetfarm"
data class RoadBudgetFarmParam (
    @SerializedName("af_id")
    val roadBudgetFarmAfId: String,
    @SerializedName("bundle_id")
    val roadBudgetFarmBundleId: String = ROAD_BUDGET_FARM_A,
    @SerializedName("os")
    val roadBudgetFarmOs: String = "Android",
    @SerializedName("store_id")
    val roadBudgetFarmStoreId: String = ROAD_BUDGET_FARM_A,
    @SerializedName("locale")
    val roadBudgetFarmLocale: String,
    @SerializedName("push_token")
    val roadBudgetFarmPushToken: String,
    @SerializedName("firebase_project_id")
    val roadBudgetFarmFirebaseProjectId: String = ROAD_BUDGET_FARM_B,

    )