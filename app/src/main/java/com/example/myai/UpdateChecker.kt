package com.example.myai

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object UpdateChecker {

    private const val VERSION_URL = "https://api.github.com/repos/richardbrownmiami-commits/MyAI-Android/releases/latest"
    const val CURRENT_VERSION = "1.0.0" // bump this on each release

    suspend fun checkForUpdate(): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val response = URL(VERSION_URL).readText()
            val json = JSONObject(response)
            val latestVersion = json.getString("tag_name").removePrefix("v")
            val assets = json.getJSONArray("assets")
            if (assets.length() == 0) return@withContext null
            val apkUrl = assets.getJSONObject(0).getString("browser_download_url")
            if (latestVersion != CURRENT_VERSION) {
                UpdateInfo(latestVersion, apkUrl)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun downloadApk(context: Context, apkUrl: String, version: String) {
        val request = DownloadManager.Request(Uri.parse(apkUrl)).apply {
            setTitle("MyAI Android v$version")
            setDescription("Downloading update...")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "MyAI-v$version.apk")
            setMimeType("application/vnd.android.package-archive")
        }
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
    }
}

data class UpdateInfo(val version: String, val apkUrl: String)

