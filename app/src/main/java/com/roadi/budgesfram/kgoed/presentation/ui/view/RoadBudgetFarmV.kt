package com.roadi.budgesfram.kgoed.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.roadi.budgesfram.kgoed.presentation.app.RoadBudgetFarmApplication
import com.roadi.budgesfram.kgoed.presentation.ui.load.RoadBudgetFarmLoadFragment
import org.koin.android.ext.android.inject

class RoadBudgetFarmV : Fragment(){

    private lateinit var roadBudgetFarmPhoto: Uri
    private var roadBudgetFarmFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val roadBudgetFarmTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        roadBudgetFarmFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        roadBudgetFarmFilePathFromChrome = null
    }

    private val roadBudgetFarmTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            roadBudgetFarmFilePathFromChrome?.onReceiveValue(arrayOf(roadBudgetFarmPhoto))
            roadBudgetFarmFilePathFromChrome = null
        } else {
            roadBudgetFarmFilePathFromChrome?.onReceiveValue(null)
            roadBudgetFarmFilePathFromChrome = null
        }
    }

    private val roadBudgetFarmDataStore by activityViewModels<RoadBudgetFarmDataStore>()


    private val roadBudgetFarmViFun by inject<RoadBudgetFarmViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (roadBudgetFarmDataStore.roadBudgetFarmView.canGoBack()) {
                        roadBudgetFarmDataStore.roadBudgetFarmView.goBack()
                        Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "WebView can go back")
                    } else if (roadBudgetFarmDataStore.roadBudgetFarmViList.size > 1) {
                        Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "WebView can`t go back")
                        roadBudgetFarmDataStore.roadBudgetFarmViList.removeAt(roadBudgetFarmDataStore.roadBudgetFarmViList.lastIndex)
                        Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "WebView list size ${roadBudgetFarmDataStore.roadBudgetFarmViList.size}")
                        roadBudgetFarmDataStore.roadBudgetFarmView.destroy()
                        val previousWebView = roadBudgetFarmDataStore.roadBudgetFarmViList.last()
                        roadBudgetFarmAttachWebViewToContainer(previousWebView)
                        roadBudgetFarmDataStore.roadBudgetFarmView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (roadBudgetFarmDataStore.roadBudgetFarmIsFirstCreate) {
            roadBudgetFarmDataStore.roadBudgetFarmIsFirstCreate = false
            roadBudgetFarmDataStore.roadBudgetFarmContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return roadBudgetFarmDataStore.roadBudgetFarmContainerView
        } else {
            return roadBudgetFarmDataStore.roadBudgetFarmContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "onViewCreated")
        if (roadBudgetFarmDataStore.roadBudgetFarmViList.isEmpty()) {
            roadBudgetFarmDataStore.roadBudgetFarmView = RoadBudgetFarmVi(requireContext(), object :
                RoadBudgetFarmCallBack {
                override fun roadBudgetFarmHandleCreateWebWindowRequest(roadBudgetFarmVi: RoadBudgetFarmVi) {
                    roadBudgetFarmDataStore.roadBudgetFarmViList.add(roadBudgetFarmVi)
                    Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "WebView list size = ${roadBudgetFarmDataStore.roadBudgetFarmViList.size}")
                    Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "CreateWebWindowRequest")
                    roadBudgetFarmDataStore.roadBudgetFarmView = roadBudgetFarmVi
                    roadBudgetFarmVi.roadBudgetFarmSetFileChooserHandler { callback ->
                        roadBudgetFarmHandleFileChooser(callback)
                    }
                    roadBudgetFarmAttachWebViewToContainer(roadBudgetFarmVi)
                }

            }, roadBudgetFarmWindow = requireActivity().window).apply {
                roadBudgetFarmSetFileChooserHandler { callback ->
                    roadBudgetFarmHandleFileChooser(callback)
                }
            }
            roadBudgetFarmDataStore.roadBudgetFarmView.roadBudgetFarmFLoad(arguments?.getString(
                RoadBudgetFarmLoadFragment.ROAD_BUDGET_FARM_D) ?: "")
//            ejvview.fLoad("www.google.com")
            roadBudgetFarmDataStore.roadBudgetFarmViList.add(roadBudgetFarmDataStore.roadBudgetFarmView)
            roadBudgetFarmAttachWebViewToContainer(roadBudgetFarmDataStore.roadBudgetFarmView)
        } else {
            roadBudgetFarmDataStore.roadBudgetFarmViList.forEach { webView ->
                webView.roadBudgetFarmSetFileChooserHandler { callback ->
                    roadBudgetFarmHandleFileChooser(callback)
                }
            }
            roadBudgetFarmDataStore.roadBudgetFarmView = roadBudgetFarmDataStore.roadBudgetFarmViList.last()

            roadBudgetFarmAttachWebViewToContainer(roadBudgetFarmDataStore.roadBudgetFarmView)
        }
        Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "WebView list size = ${roadBudgetFarmDataStore.roadBudgetFarmViList.size}")
    }

    private fun roadBudgetFarmHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        roadBudgetFarmFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "Launching file picker")
                    roadBudgetFarmTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "Launching camera")
                    roadBudgetFarmPhoto = roadBudgetFarmViFun.roadBudgetFarmSavePhoto()
                    roadBudgetFarmTakePhoto.launch(roadBudgetFarmPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                roadBudgetFarmFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun roadBudgetFarmAttachWebViewToContainer(w: RoadBudgetFarmVi) {
        roadBudgetFarmDataStore.roadBudgetFarmContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            roadBudgetFarmDataStore.roadBudgetFarmContainerView.removeAllViews()
            roadBudgetFarmDataStore.roadBudgetFarmContainerView.addView(w)
        }
    }


}