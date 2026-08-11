package com.stepbeatgo.app.data.model

/**
 * A music genre with an average tempo. Fully static, bundled with the app —
 * no network lookup involved. Values are commonly cited average BPM ranges
 * for each style; the midpoint is used as the default.
 */
data class Genre(
    val id: String,
    val nameRes: String,
    val bpm: Int
)

/**
 * Built-in genre catalog. Kept as plain data (not a resource file) so the
 * BPM numbers are easy to tweak without touching UI code.
 */
object GenreCatalog {
    val all: List<Genre> = listOf(
        Genre("lofi", "Lo-fi", 80),
        Genre("reggae", "Reggae", 75),
        Genre("ballad", "Ballad", 65),
        Genre("classical", "Classical", 90),
        Genre("rnb", "R&B", 95),
        Genre("pop", "Pop", 110),
        Genre("indie", "Indie rock", 118),
        Genre("funk", "Funk", 112),
        Genre("disco", "Disco", 120),
        Genre("house", "House", 124),
        Genre("techno", "Techno", 130),
        Genre("trance", "Trance", 138),
        Genre("metal", "Metal", 150),
        Genre("punk", "Punk", 165),
        Genre("dnb", "Drum & Bass", 170),
        Genre("hardcore", "Hardcore", 180)
    )

    fun byId(id: String): Genre? = all.find { it.id == id }
}
