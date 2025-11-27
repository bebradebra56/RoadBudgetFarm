package com.roadi.budgesfram.kgoed.data.repo

import android.util.Log
import com.roadi.budgesfram.kgoed.domain.model.RoadBudgetFarmEntity
import com.roadi.budgesfram.kgoed.domain.model.RoadBudgetFarmParam
import com.roadi.budgesfram.kgoed.presentation.app.RoadBudgetFarmApplication.Companion.ROAD_BUDGET_FARM_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RoadBudgetFarmApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun roadBudgetFarmGetClient(
        @Body jsonString: JsonObject,
    ): Call<RoadBudgetFarmEntity>
}


private const val ROAD_BUDGET_FARM_MAIN = "https://roadbudgetfarm.com/"
class RoadBudgetFarmRepository {

    suspend fun roadBudgetFarmGetClient(
        roadBudgetFarmParam: RoadBudgetFarmParam,
        roadBudgetFarmConversion: MutableMap<String, Any>?
    ): RoadBudgetFarmEntity? {
        val gson = Gson()
        val api = roadBudgetFarmGetApi(ROAD_BUDGET_FARM_MAIN, null)

        val roadBudgetFarmJsonObject = gson.toJsonTree(roadBudgetFarmParam).asJsonObject
        roadBudgetFarmConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            roadBudgetFarmJsonObject.add(key, element)
        }
        return try {
            val roadBudgetFarmRequest: Call<RoadBudgetFarmEntity> = api.roadBudgetFarmGetClient(
                jsonString = roadBudgetFarmJsonObject,
            )
            val roadBudgetFarmResult = roadBudgetFarmRequest.awaitResponse()
            Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "Retrofit: Result code: ${roadBudgetFarmResult.code()}")
            if (roadBudgetFarmResult.code() == 200) {
                Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "Retrofit: Get request success")
                Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "Retrofit: Code = ${roadBudgetFarmResult.code()}")
                Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "Retrofit: ${roadBudgetFarmResult.body()}")
                roadBudgetFarmResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun roadBudgetFarmGetApi(url: String, client: OkHttpClient?) : RoadBudgetFarmApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
