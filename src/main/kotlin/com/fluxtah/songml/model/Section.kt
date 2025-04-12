package com.fluxtah.songml.model

data class Section(
    val name: String,
    val bars: Int,
    val startBar: Int = 0,
    val startTimeSeconds: Double = 0.0,
    val lines: List<ParsedLine> = emptyList()
)