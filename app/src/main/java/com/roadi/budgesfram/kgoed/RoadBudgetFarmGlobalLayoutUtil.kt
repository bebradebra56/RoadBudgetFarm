package com.roadi.budgesfram.kgoed

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.roadi.budgesfram.kgoed.presentation.app.RoadBudgetFarmApplication

class RoadBudgetFarmGlobalLayoutUtil {

    private var roadBudgetFarmMChildOfContent: View? = null
    private var roadBudgetFarmUsableHeightPrevious = 0

    fun roadBudgetFarmAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        roadBudgetFarmMChildOfContent = content.getChildAt(0)

        roadBudgetFarmMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val roadBudgetFarmUsableHeightNow = roadBudgetFarmComputeUsableHeight()
        if (roadBudgetFarmUsableHeightNow != roadBudgetFarmUsableHeightPrevious) {
            val roadBudgetFarmUsableHeightSansKeyboard = roadBudgetFarmMChildOfContent?.rootView?.height ?: 0
            val roadBudgetFarmHeightDifference = roadBudgetFarmUsableHeightSansKeyboard - roadBudgetFarmUsableHeightNow

            if (roadBudgetFarmHeightDifference > (roadBudgetFarmUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(RoadBudgetFarmApplication.roadBudgetFarmInputMode)
            } else {
                activity.window.setSoftInputMode(RoadBudgetFarmApplication.roadBudgetFarmInputMode)
            }
//            mChildOfContent?.requestLayout()
            roadBudgetFarmUsableHeightPrevious = roadBudgetFarmUsableHeightNow
        }
    }

    private fun roadBudgetFarmComputeUsableHeight(): Int {
        val r = Rect()
        roadBudgetFarmMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}