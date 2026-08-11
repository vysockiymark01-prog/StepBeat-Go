package com.stepbeatgo.app.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class LatLng(val lat: Double, val lng: Double)

data class ParsedRoute(
    val origin: LatLng,
    val destination: LatLng
) {
    /** Straight-line ("as the crow flies") distance in kilometers, via the
     * haversine formula. This is an approximation — real streets are longer —
     * used only as a fallback when the user hasn't typed a real walking time
     * from their maps app. */
    val straightLineKm: Double
        get() = haversineKm(origin, destination)
}

/**
 * Pulls two coordinate pairs out of a route link copied from a maps app.
 * Purely local regex parsing of the URL text the user pastes — no network
 * request is made, and the link itself is never fetched.
 *
 * Recognizes the common patterns used by Google Maps, Yandex Maps and 2GIS
 * share/deep links. If a link doesn't match, callers should fall back to
 * asking the user to type the time manually.
 */
object RouteLinkParser {

    private val coordPair = Regex("""(-?\d{1,3}\.\d+),\s*(-?\d{1,3}\.\d+)""")

    fun parse(rawText: String): ParsedRoute? {
        val text = rawText.trim()
        if (text.isEmpty()) return null

        // Google Maps: .../dir/55.751244,37.618423/55.755826,37.617300/...
        // or ?origin=lat,lng&destination=lat,lng
        parseGoogleStyle(text)?.let { return it }

        // Yandex Maps: ...rtext=55.751244,37.618423~55.755826,37.617300...
        parseYandexStyle(text)?.let { return it }

        // Generic fallback: if the link (or pasted text) simply contains two
        // coordinate pairs anywhere, take the first two — covers 2GIS and
        // other formats that embed raw lat,lng pairs in the query string.
        val matches = coordPair.findAll(text).toList()
        if (matches.size >= 2) {
            val first = matches[0]
            val second = matches[1]
            return ParsedRoute(
                origin = LatLng(first.groupValues[1].toDouble(), first.groupValues[2].toDouble()),
                destination = LatLng(second.groupValues[1].toDouble(), second.groupValues[2].toDouble())
            )
        }
        return null
    }

    private fun parseGoogleStyle(text: String): ParsedRoute? {
        val originParam = Regex("""origin=(-?\d{1,3}\.\d+),(-?\d{1,3}\.\d+)""").find(text)
        val destParam = Regex("""destination=(-?\d{1,3}\.\d+),(-?\d{1,3}\.\d+)""").find(text)
        if (originParam != null && destParam != null) {
            return ParsedRoute(
                origin = LatLng(originParam.groupValues[1].toDouble(), originParam.groupValues[2].toDouble()),
                destination = LatLng(destParam.groupValues[1].toDouble(), destParam.groupValues[2].toDouble())
            )
        }

        val dirMatch = Regex("""/dir/(-?\d{1,3}\.\d+),(-?\d{1,3}\.\d+)/(-?\d{1,3}\.\d+),(-?\d{1,3}\.\d+)""").find(text)
        if (dirMatch != null) {
            return ParsedRoute(
                origin = LatLng(dirMatch.groupValues[1].toDouble(), dirMatch.groupValues[2].toDouble()),
                destination = LatLng(dirMatch.groupValues[3].toDouble(), dirMatch.groupValues[4].toDouble())
            )
        }
        return null
    }

    private fun parseYandexStyle(text: String): ParsedRoute? {
        val rtext = Regex("""rtext=(-?\d{1,3}\.\d+),\s*(-?\d{1,3}\.\d+)~(-?\d{1,3}\.\d+),\s*(-?\d{1,3}\.\d+)""").find(text)
        if (rtext != null) {
            return ParsedRoute(
                origin = LatLng(rtext.groupValues[1].toDouble(), rtext.groupValues[2].toDouble()),
                destination = LatLng(rtext.groupValues[3].toDouble(), rtext.groupValues[4].toDouble())
            )
        }
        return null
    }

    private fun haversineKm(a: LatLng, b: LatLng): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(b.lat - a.lat)
        val dLng = Math.toRadians(b.lng - a.lng)
        val lat1 = Math.toRadians(a.lat)
        val lat2 = Math.toRadians(b.lat)

        val h = sin(dLat / 2) * sin(dLat / 2) +
            cos(lat1) * cos(lat2) * sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(h), sqrt(1 - h))
        return earthRadiusKm * c
    }
}
