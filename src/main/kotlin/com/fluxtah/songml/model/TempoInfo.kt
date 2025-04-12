package com.fluxtah.songml.model

data class TempoInfo(
    val bpm: Double = DEFAULT_BPM,
    val timeSignature: String = "4/4", // default to common time
    val swing: Boolean = false
)