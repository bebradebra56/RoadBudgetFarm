package com.roadi.budgesfram.kgoed.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class RoadBudgetFarmDataStore : ViewModel(){
    val roadBudgetFarmViList: MutableList<RoadBudgetFarmVi> = mutableListOf()
    var roadBudgetFarmIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var roadBudgetFarmContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var roadBudgetFarmView: RoadBudgetFarmVi

}