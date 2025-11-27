package com.roadi.budgesfram.kgoed.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.roadi.budgesfram.data.database.DatabaseInitializer
import com.roadi.budgesfram.di.appModule
import com.roadi.budgesfram.kgoed.presentation.di.roadBudgetFarmModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface RoadBudgetFarmAppsFlyerState {
    data object RoadBudgetFarmDefault : RoadBudgetFarmAppsFlyerState
    data class RoadBudgetFarmSuccess(val roadBudgetFarmData: MutableMap<String, Any>?) :
        RoadBudgetFarmAppsFlyerState

    data object RoadBudgetFarmError : RoadBudgetFarmAppsFlyerState
}

interface RoadBudgetFarmAppsApi {
    @Headers("Content-Type: application/json")
    @GET(ROAD_BUDGET_FARM_LIN)
    fun roadBudgetFarmGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val ROAD_BUDGET_FARM_APP_DEV = "xVagntBVKmbaWeqouqijAR"
private const val ROAD_BUDGET_FARM_LIN = "com.roadi.budgesfram"

class RoadBudgetFarmApplication : Application() {
    private var roadBudgetFarmIsResumed = false
    private var roadBudgetFarmConversionTimeoutJob: Job? = null
    private var roadBudgetFarmDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        roadBudgetFarmSetDebufLogger(appsflyer)
        roadBudgetFarmMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        roadBudgetFarmExtractDeepMap(p0.deepLink)
                        Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            ROAD_BUDGET_FARM_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    roadBudgetFarmConversionTimeoutJob?.cancel()
                    Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = roadBudgetFarmGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.roadBudgetFarmGetClient(
                                    devkey = ROAD_BUDGET_FARM_APP_DEV,
                                    deviceId = roadBudgetFarmGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    roadBudgetFarmResume(RoadBudgetFarmAppsFlyerState.RoadBudgetFarmError)
                                } else {
                                    roadBudgetFarmResume(
                                        RoadBudgetFarmAppsFlyerState.RoadBudgetFarmSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "Error: ${d.message}")
                                roadBudgetFarmResume(RoadBudgetFarmAppsFlyerState.RoadBudgetFarmError)
                            }
                        }
                    } else {
                        roadBudgetFarmResume(RoadBudgetFarmAppsFlyerState.RoadBudgetFarmSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    roadBudgetFarmConversionTimeoutJob?.cancel()
                    Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "onConversionDataFail: $p0")
                    roadBudgetFarmResume(RoadBudgetFarmAppsFlyerState.RoadBudgetFarmError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, ROAD_BUDGET_FARM_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        roadBudgetFarmStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@RoadBudgetFarmApplication)
            modules(
                listOf(
                    roadBudgetFarmModule, appModule
                )
            )
        }
        // Initialize database
        val databaseInitializer: DatabaseInitializer by inject()
        databaseInitializer.initializeDatabase()
    }

    private fun roadBudgetFarmExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "Extracted DeepLink data: $map")
        roadBudgetFarmDeepLinkData = map
    }

    private fun roadBudgetFarmStartConversionTimeout() {
        roadBudgetFarmConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!roadBudgetFarmIsResumed) {
                Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                roadBudgetFarmResume(RoadBudgetFarmAppsFlyerState.RoadBudgetFarmError)
            }
        }
    }

    private fun roadBudgetFarmResume(state: RoadBudgetFarmAppsFlyerState) {
        roadBudgetFarmConversionTimeoutJob?.cancel()
        if (state is RoadBudgetFarmAppsFlyerState.RoadBudgetFarmSuccess) {
            val convData = state.roadBudgetFarmData ?: mutableMapOf()
            val deepData = roadBudgetFarmDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!roadBudgetFarmIsResumed) {
                roadBudgetFarmIsResumed = true
                roadBudgetFarmConversionFlow.value = RoadBudgetFarmAppsFlyerState.RoadBudgetFarmSuccess(merged)
            }
        } else {
            if (!roadBudgetFarmIsResumed) {
                roadBudgetFarmIsResumed = true
                roadBudgetFarmConversionFlow.value = state
            }
        }
    }

    private fun roadBudgetFarmGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(ROAD_BUDGET_FARM_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun roadBudgetFarmSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun roadBudgetFarmMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun roadBudgetFarmGetApi(url: String, client: OkHttpClient?): RoadBudgetFarmAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        var roadBudgetFarmInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val roadBudgetFarmConversionFlow: MutableStateFlow<RoadBudgetFarmAppsFlyerState> = MutableStateFlow(
            RoadBudgetFarmAppsFlyerState.RoadBudgetFarmDefault
        )
        var ROAD_BUDGET_FARM_FB_LI: String? = null
        const val ROAD_BUDGET_FARM_MAIN_TAG = "RoadBudgetFarmMainTag"
    }
}