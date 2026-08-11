package com.stepbeatgo.app.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.net.toUri

enum class MapsProvider(val displayName: String, val packageName: String) {
    YANDEX_MAPS("Yandex Maps", "ru.yandex.yandexmaps"),
    DGIS("2GIS", "ru.dublgis.dgismobile"),
    GOOGLE_MAPS("Google Maps", "com.google.android.apps.maps")
}

/**
 * Opens a walking route in the user's chosen maps app via a deep link — no
 * API key, no network call made by this app. The maps app itself resolves
 * the route. If nothing is installed, falls back to the generic web URL,
 * which the system will open in a browser.
 */
object MapsAppLauncher {

    fun installedProviders(context: Context): List<MapsProvider> {
        val pm = context.packageManager
        return MapsProvider.entries.filter { isInstalled(pm, it.packageName) }
    }

    private fun isInstalled(pm: PackageManager, packageName: String): Boolean =
        try {
            pm.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    fun routeUri(provider: MapsProvider, originQuery: String, destinationQuery: String): Uri =
        when (provider) {
            MapsProvider.YANDEX_MAPS ->
                "https://yandex.ru/maps/?rtext=${Uri.encode(originQuery)}~${Uri.encode(destinationQuery)}&rtt=pd".toUri()
            MapsProvider.DGIS ->
                "https://2gis.ru/directions/points/${Uri.encode(originQuery)}~${Uri.encode(destinationQuery)}".toUri()
            MapsProvider.GOOGLE_MAPS ->
                "https://www.google.com/maps/dir/?api=1&origin=${Uri.encode(originQuery)}&destination=${Uri.encode(destinationQuery)}&travelmode=walking".toUri()
        }

    fun openRoute(context: Context, provider: MapsProvider, originQuery: String, destinationQuery: String): Boolean {
        val uri = routeUri(provider, originQuery, destinationQuery)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage(provider.packageName)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                true
            } catch (e2: ActivityNotFoundException) {
                false
            }
        }
    }
}
