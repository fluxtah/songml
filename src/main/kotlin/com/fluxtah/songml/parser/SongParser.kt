package com.fluxtah.songml.parser

import com.fluxtah.songml.model.DEFAULT_BPM
import com.fluxtah.songml.model.ParsedLine
import com.fluxtah.songml.model.Section
import com.fluxtah.songml.model.Song
import com.fluxtah.songml.model.TempoInfo

object SongParser {
    fun parse(lines: List<String>): Song {
        val sections = mutableListOf<Section>()
        var tempoInfo: TempoInfo? = null
        var extraInfo = mutableMapOf<String, String>()

        var currentSectionName: String? = null
        var currentBars = 0
        var currentLines = mutableListOf<ParsedLine>()
        var totalBars = 0
        var barDuration = 0.0

        var inExtraSection = false
        var inTempoSection = false
        var tempoBPM = DEFAULT_BPM
        var tempoSig = "4/4"

        for (line in lines.map { it.trim() }) {
            // --- Handle tempo block first ---
            if (inTempoSection) {
                if (line.startsWith("[") || line.isBlank()) {
                    // End of tempo block
                    tempoInfo = TempoInfo(tempoBPM, tempoSig)
                    barDuration = barDurationInSeconds(tempoBPM, tempoSig)
                    inTempoSection = false
                    // fall through to handle [Intro], [Verse], etc.
                } else if (line.contains(":")) {
                    val (key, value) = line.split(":", limit = 2)
                    when (key.trim().lowercase()) {
                        "bpm" -> tempoBPM = value.trim().toDoubleOrNull() ?: DEFAULT_BPM
                        "timesig", "time signature" -> tempoSig = value.trim()
                        // swing could be parsed here too if desired
                    }
                    continue
                } else {
                    continue // skip anything unknown in tempo block
                }
            }

            // --- Normal line handling ---
            when {
                line.equals("[Tempo]", ignoreCase = true) -> {
                    inTempoSection = true
                    tempoBPM = DEFAULT_BPM
                    tempoSig = "4/4"
                }

                line.equals("[Extra]", ignoreCase = true) -> {
                    // Flush current section before switching to Extra
                    currentSectionName?.let {
                        sections.add(
                            Section(
                                name = it,
                                bars = currentBars,
                                startBar = totalBars,
                                startTimeSeconds = totalBars * barDuration,
                                lines = currentLines.toList()
                            )
                        )
                        totalBars += currentBars
                        currentSectionName = null
                    }
                    inExtraSection = true
                }

                line.startsWith("[") && line.contains("]") -> {
                    // Flush previous section
                    currentSectionName?.let {
                        sections.add(
                            Section(
                                name = it,
                                bars = currentBars,
                                startBar = totalBars,
                                startTimeSeconds = totalBars * barDuration,
                                lines = currentLines.toList()
                            )
                        )
                        totalBars += currentBars
                    }

                    val header = line.removePrefix("[").removeSuffix("]").trim()
                    val parts = header.split(Regex("\\s+"))

                    val maybeBars = parts.lastOrNull()?.toIntOrNull()
                    currentBars = maybeBars ?: 0
                    currentSectionName = if (maybeBars != null) {
                        parts.dropLast(1).joinToString(" ")
                    } else {
                        parts.joinToString(" ")
                    }

                    if (currentBars == 0) {
                        println("⚠️ Warning: Section [$currentSectionName] parsed with 0 bars.")
                    }

                    currentLines = mutableListOf()
                    inExtraSection = false
                    inTempoSection = false
                }

                inExtraSection && line.contains(":") -> {
                    val (key, value) = line.split(":", limit = 2)
                    extraInfo[key.trim()] = value.trim()
                }

                line.isNotEmpty() && !inExtraSection && !inTempoSection -> {
                    currentLines.add(LineParser.parse(line))
                }
            }
        }

        // Final section flush
        currentSectionName?.let {
            sections.add(
                Section(
                    name = it,
                    bars = currentBars,
                    startBar = totalBars,
                    startTimeSeconds = totalBars * barDuration,
                    lines = currentLines.toList()
                )
            )
            totalBars += currentBars
        }

        return Song(
            sections = sections,
            totalBars = totalBars,
            tempo = tempoInfo,
            extra = extraInfo
        )
    }

    private fun barDurationInSeconds(bpm: Double, timeSig: String): Double {
        val beatsPerBar = timeSig.split("/").firstOrNull()?.toIntOrNull() ?: 4
        val secondsPerBeat = 60.0 / bpm
        return beatsPerBar * secondsPerBeat
    }
}
