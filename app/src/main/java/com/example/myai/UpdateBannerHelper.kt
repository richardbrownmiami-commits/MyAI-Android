package com.example.myai

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object UpdateBannerHelper {

    fun showIfUpdateAvailable(
        activity: Activity,
        rootView: View,
        scope: CoroutineScope
    ) {
        scope.launch {
            val updateInfo = UpdateChecker.checkForUpdate() ?: return@launch
            activity.runOnUiThread {
                showBanner(activity, rootView, updateInfo)
            }
        }
    }

    private fun showBanner(activity: Activity, rootView: View, updateInfo: UpdateInfo) {
        val context = activity
        val banner = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor("#1A73E8"))
            setPadding(32, 20, 32, 20)
            id = View.generateViewId()
        }

        val messageText = TextView(context).apply {
            text = "Update v${updateInfo.version} available!"
            setTextColor(Color.WHITE)
            textSize = 14f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val downloadBtn = Button(context).apply {
            text = "Download"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.TRANSPARENT)
            textSize = 13f
            setOnClickListener {
                UpdateChecker.downloadApk(context, updateInfo.apkUrl, updateInfo.version)
            }
        }

        val dismissBtn = Button(context).apply {
            text = "Dismiss"
            setTextColor(Color.parseColor("#BBDEFB"))
            setBackgroundColor(Color.TRANSPARENT)
            textSize = 13f
            setOnClickListener {
                (banner.parent as? android.view.ViewGroup)?.removeView(banner)
            }
        }

        banner.addView(messageText)
        banner.addView(downloadBtn)
        banner.addView(dismissBtn)

        // Insert at the top of the root layout
        if (rootView is android.view.ViewGroup) {
            rootView.addView(banner, 0)
        }
    }
}

