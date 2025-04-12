package com.fluxtah.songml.model

import com.fluxtah.songml.model.TempoInfo

data class Song(
    val sections: List<Section>,
    val totalBars: Int = 0,
    val tempo: TempoInfo? = null,
    val extra: Map<String, String> = emptyMap()
)