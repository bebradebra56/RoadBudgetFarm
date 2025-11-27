package com.roadi.budgesfram

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.roadi.budgesfram.kgoed.RoadBudgetFarmGlobalLayoutUtil
import com.roadi.budgesfram.kgoed.presentation.app.RoadBudgetFarmApplication
import com.roadi.budgesfram.kgoed.presentation.pushhandler.RoadBudgetFarmPushHandler
import com.roadi.budgesfram.kgoed.roadBudgetFarmSetupSystemBars
import org.koin.android.ext.android.inject

class RoadBudgetFarmActivity : AppCompatActivity() {

    private val roadBudgetFarmPushHandler by inject<RoadBudgetFarmPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        roadBudgetFarmSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_road_budget_farm)

        val roadBudgetFarmRootView = findViewById<View>(android.R.id.content)
        RoadBudgetFarmGlobalLayoutUtil().roadBudgetFarmAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(roadBudgetFarmRootView) { roadBudgetFarmView, roadBudgetFarmInsets ->
            val roadBudgetFarmSystemBars = roadBudgetFarmInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val roadBudgetFarmDisplayCutout = roadBudgetFarmInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val roadBudgetFarmIme = roadBudgetFarmInsets.getInsets(WindowInsetsCompat.Type.ime())


            val roadBudgetFarmTopPadding = maxOf(roadBudgetFarmSystemBars.top, roadBudgetFarmDisplayCutout.top)
            val roadBudgetFarmLeftPadding = maxOf(roadBudgetFarmSystemBars.left, roadBudgetFarmDisplayCutout.left)
            val roadBudgetFarmRightPadding = maxOf(roadBudgetFarmSystemBars.right, roadBudgetFarmDisplayCutout.right)
            window.setSoftInputMode(RoadBudgetFarmApplication.roadBudgetFarmInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "ADJUST PUN")
                val roadBudgetFarmBottomInset = maxOf(roadBudgetFarmSystemBars.bottom, roadBudgetFarmDisplayCutout.bottom)

                roadBudgetFarmView.setPadding(roadBudgetFarmLeftPadding, roadBudgetFarmTopPadding, roadBudgetFarmRightPadding, 0)

                roadBudgetFarmView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = roadBudgetFarmBottomInset
                }
            } else {
                Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "ADJUST RESIZE")

                val roadBudgetFarmBottomInset = maxOf(roadBudgetFarmSystemBars.bottom, roadBudgetFarmDisplayCutout.bottom, roadBudgetFarmIme.bottom)

                roadBudgetFarmView.setPadding(roadBudgetFarmLeftPadding, roadBudgetFarmTopPadding, roadBudgetFarmRightPadding, 0)

                roadBudgetFarmView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = roadBudgetFarmBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "Activity onCreate()")
        roadBudgetFarmPushHandler.roadBudgetFarmHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            roadBudgetFarmSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        roadBudgetFarmSetupSystemBars()
    }
}