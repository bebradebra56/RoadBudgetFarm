package com.roadi.budgesfram.kgoed.presentation.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import com.roadi.budgesfram.kgoed.presentation.app.RoadBudgetFarmApplication

class RoadBudgetFarmVi(
    private val roadBudgetFarmContext: Context,
    private val roadBudgetFarmCallback: RoadBudgetFarmCallBack,
    private val roadBudgetFarmWindow: Window
) : WebView(roadBudgetFarmContext) {
    private var roadBudgetFarmFileChooserHandler: ((ValueCallback<Array<Uri>>?) -> Unit)? = null
    fun roadBudgetFarmSetFileChooserHandler(handler: (ValueCallback<Array<Uri>>?) -> Unit) {
        this.roadBudgetFarmFileChooserHandler = handler
    }
    init {
        val webSettings = settings
        webSettings.apply {
            setSupportMultipleWindows(true)
            allowFileAccess = true
            allowContentAccess = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            userAgentString = WebSettings.getDefaultUserAgent(roadBudgetFarmContext).replace("; wv)", "").replace("Version/4.0 ", "")
            @SuppressLint("SetJavaScriptEnabled")
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        isNestedScrollingEnabled = true



        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        super.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                val link = request?.url?.toString() ?: ""

                return if (request?.isRedirect == true) {
                    view?.loadUrl(request?.url.toString())
                    true
                }
                else if (URLUtil.isNetworkUrl(link)) {
                    false
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    try {
                        roadBudgetFarmContext.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(roadBudgetFarmContext, "This application not found", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                CookieManager.getInstance().flush()
                if (url?.contains("ninecasino") == true) {
                    RoadBudgetFarmApplication.roadBudgetFarmInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                    Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "onPageFinished : ${RoadBudgetFarmApplication.roadBudgetFarmInputMode}")
                    roadBudgetFarmWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                } else {
                    RoadBudgetFarmApplication.roadBudgetFarmInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    Log.d(RoadBudgetFarmApplication.ROAD_BUDGET_FARM_MAIN_TAG, "onPageFinished : ${RoadBudgetFarmApplication.roadBudgetFarmInputMode}")
                    roadBudgetFarmWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }


        })

        super.setWebChromeClient(object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: WebChromeClient.FileChooserParams?,
            ): Boolean {
                roadBudgetFarmFileChooserHandler?.invoke(filePathCallback)
                return true
            }
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                roadBudgetFarmHandleCreateWebWindowRequest(resultMsg)
                return true
            }
        })
    }


    fun roadBudgetFarmFLoad(link: String) {
        super.loadUrl(link)
    }

    private fun roadBudgetFarmHandleCreateWebWindowRequest(resultMsg: Message?) {
        if (resultMsg == null) return
        if (resultMsg.obj != null && resultMsg.obj is WebView.WebViewTransport) {
            val transport = resultMsg.obj as WebView.WebViewTransport
            val windowWebView = RoadBudgetFarmVi(roadBudgetFarmContext, roadBudgetFarmCallback, roadBudgetFarmWindow)
            transport.webView = windowWebView
            resultMsg.sendToTarget()
            roadBudgetFarmCallback.roadBudgetFarmHandleCreateWebWindowRequest(windowWebView)
        }
    }

}