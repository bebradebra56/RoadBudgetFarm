package com.roadi.budgesfram.kgoed.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.roadi.budgesfram.kgoed.presentation.app.RoadBudgetFarmApplication

class RoadBudgetFarmPushHandler {
    fun roadBudgetFarmHandlePush(extras: Bundle?) {
        Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = roadBudgetFarmBundleToMap(extras)
            Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    RoadBudgetFarmApplication.ROAD_BUDGET_FARM_FB_LI = map["url"]
                    Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "Push data no!")
        }
    }

    private fun roadBudgetFarmBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}