package com.roadi.budgesfram.kgoed.domain.model

import com.google.gson.annotations.SerializedName


data class RoadBudgetFarmEntity (
    @SerializedName("ok")
    val roadBudgetFarmOk: String,
    @SerializedName("url")
    val roadBudgetFarmUrl: String,
    @SerializedName("expires")
    val roadBudgetFarmExpires: Long,
)