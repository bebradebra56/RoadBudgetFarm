package com.roadi.budgesfram.kgoed.data.shar

import android.content.Context
import androidx.core.content.edit

class RoadBudgetFarmSharedPreference(context: Context) {
    private val roadBudgetFarmPrefs = context.getSharedPreferences("roadBudgetFarmSharedPrefsAb", Context.MODE_PRIVATE)

    var roadBudgetFarmSavedUrl: String
        get() = roadBudgetFarmPrefs.getString(ROAD_BUDGET_FARM_SAVED_URL, "") ?: ""
        set(value) = roadBudgetFarmPrefs.edit { putString(ROAD_BUDGET_FARM_SAVED_URL, value) }

    var roadBudgetFarmExpired : Long
        get() = roadBudgetFarmPrefs.getLong(ROAD_BUDGET_FARM_EXPIRED, 0L)
        set(value) = roadBudgetFarmPrefs.edit { putLong(ROAD_BUDGET_FARM_EXPIRED, value) }

    var roadBudgetFarmAppState: Int
        get() = roadBudgetFarmPrefs.getInt(ROAD_BUDGET_FARM_APPLICATION_STATE, 0)
        set(value) = roadBudgetFarmPrefs.edit { putInt(ROAD_BUDGET_FARM_APPLICATION_STATE, value) }

    var roadBudgetFarmNotificationRequest: Long
        get() = roadBudgetFarmPrefs.getLong(ROAD_BUDGET_FARM_NOTIFICAITON_REQUEST, 0L)
        set(value) = roadBudgetFarmPrefs.edit { putLong(ROAD_BUDGET_FARM_NOTIFICAITON_REQUEST, value) }

    var roadBudgetFarmNotificationRequestedBefore: Boolean
        get() = roadBudgetFarmPrefs.getBoolean(ROAD_BUDGET_FARM_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = roadBudgetFarmPrefs.edit { putBoolean(
            ROAD_BUDGET_FARM_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val ROAD_BUDGET_FARM_SAVED_URL = "roadBudgetFarmSavedUrl"
        private const val ROAD_BUDGET_FARM_EXPIRED = "roadBudgetFarmExpired"
        private const val ROAD_BUDGET_FARM_APPLICATION_STATE = "roadBudgetFarmApplicationState"
        private const val ROAD_BUDGET_FARM_NOTIFICAITON_REQUEST = "roadBudgetFarmNotificationRequest"
        private const val ROAD_BUDGET_FARM_NOTIFICATION_REQUEST_BEFORE = "roadBudgetFarmNotificationRequestedBefore"
    }
}