package com.roadi.budgesfram.kgoed.data.utils

import android.util.Log
import com.roadi.budgesfram.kgoed.presentation.app.RoadBudgetFarmApplication
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RoadBudgetFarmPushToken {

    suspend fun roadBudgetFarmGetToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume(it.result)
                    Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}