package com.roadi.budgesfram.kgoed.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.roadi.budgesfram.MainActivity
import com.roadi.budgesfram.R
import com.roadi.budgesfram.databinding.FragmentLoadRoadBudgetFarmBinding
import com.roadi.budgesfram.kgoed.data.shar.RoadBudgetFarmSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class RoadBudgetFarmLoadFragment : Fragment(R.layout.fragment_load_road_budget_farm) {
    private lateinit var roadBudgetFarmLoadBinding: FragmentLoadRoadBudgetFarmBinding

    private val roadBudgetFarmLoadViewModel by viewModel<RoadBudgetFarmLoadViewModel>()

    private val roadBudgetFarmSharedPreference by inject<RoadBudgetFarmSharedPreference>()

    private var roadBudgetFarmUrl = ""

    private val roadBudgetFarmRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            roadBudgetFarmNavigateToSuccess(roadBudgetFarmUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                roadBudgetFarmSharedPreference.roadBudgetFarmNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                roadBudgetFarmNavigateToSuccess(roadBudgetFarmUrl)
            } else {
                roadBudgetFarmNavigateToSuccess(roadBudgetFarmUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        roadBudgetFarmLoadBinding = FragmentLoadRoadBudgetFarmBinding.bind(view)

        roadBudgetFarmLoadBinding.roadBudgetFarmGrandButton.setOnClickListener {
            val roadBudgetFarmPermission = Manifest.permission.POST_NOTIFICATIONS
            roadBudgetFarmRequestNotificationPermission.launch(roadBudgetFarmPermission)
            roadBudgetFarmSharedPreference.roadBudgetFarmNotificationRequestedBefore = true
        }

        roadBudgetFarmLoadBinding.roadBudgetFarmSkipButton.setOnClickListener {
            roadBudgetFarmSharedPreference.roadBudgetFarmNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            roadBudgetFarmNavigateToSuccess(roadBudgetFarmUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                roadBudgetFarmLoadViewModel.roadBudgetFarmHomeScreenState.collect {
                    when (it) {
                        is RoadBudgetFarmLoadViewModel.RoadBudgetFarmHomeScreenState.RoadBudgetFarmLoading -> {

                        }

                        is RoadBudgetFarmLoadViewModel.RoadBudgetFarmHomeScreenState.RoadBudgetFarmError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is RoadBudgetFarmLoadViewModel.RoadBudgetFarmHomeScreenState.RoadBudgetFarmSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val roadBudgetFarmPermission = Manifest.permission.POST_NOTIFICATIONS
                                val roadBudgetFarmPermissionRequestedBefore = roadBudgetFarmSharedPreference.roadBudgetFarmNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), roadBudgetFarmPermission) == PackageManager.PERMISSION_GRANTED) {
                                    roadBudgetFarmNavigateToSuccess(it.data)
                                } else if (!roadBudgetFarmPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > roadBudgetFarmSharedPreference.roadBudgetFarmNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    roadBudgetFarmLoadBinding.roadBudgetFarmNotiGroup.visibility = View.VISIBLE
                                    roadBudgetFarmLoadBinding.roadBudgetFarmLoadingGroup.visibility = View.GONE
                                    roadBudgetFarmUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(roadBudgetFarmPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > roadBudgetFarmSharedPreference.roadBudgetFarmNotificationRequest) {
                                        roadBudgetFarmLoadBinding.roadBudgetFarmNotiGroup.visibility = View.VISIBLE
                                        roadBudgetFarmLoadBinding.roadBudgetFarmLoadingGroup.visibility = View.GONE
                                        roadBudgetFarmUrl = it.data
                                    } else {
                                        roadBudgetFarmNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    roadBudgetFarmNavigateToSuccess(it.data)
                                }
                            } else {
                                roadBudgetFarmNavigateToSuccess(it.data)
                            }
                        }

                        RoadBudgetFarmLoadViewModel.RoadBudgetFarmHomeScreenState.RoadBudgetFarmNotInternet -> {
                            roadBudgetFarmLoadBinding.roadBudgetFarmStateGroup.visibility = View.VISIBLE
                            roadBudgetFarmLoadBinding.roadBudgetFarmLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun roadBudgetFarmNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_roadBudgetFarmLoadFragment_to_roadBudgetFarmV,
            bundleOf(ROAD_BUDGET_FARM_D to data)
        )
    }

    companion object {
        const val ROAD_BUDGET_FARM_D = "roadBudgetFarmData"
    }
}