package com.fluxtah.songml.writer.fcpxml

import com.fluxtah.songml.model.ParsedLine
import com.fluxtah.songml.model.Song
import com.fluxtah.songml.model.TempoInfo
import com.fluxtah.songml.model.Token
import kotlin.math.roundToInt

object FCPXMLWriter {

    private const val EFFECT_ID = "r2"
    private const val EFFECT_NAME = "Graphic Text"
    private const val EFFECT_UID = ".../Titles.localized/Basic Text.localized/Graphic Text.localized/Graphic Text.moti"

    fun generateFCPXML(song: Song, audioFilename: String? = null, fps: Double = 30.0): String {
        val formatId = "r1"
        val assetId = "a1"
        val barDuration = getBarDuration(song)
        val totalDuration = song.totalBars * barDuration
        val frameDuration = frameDurationString(fps)

        val xml = StringBuilder()
        xml.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
        xml.appendLine("""<!DOCTYPE fcpxml>""")
        xml.appendLine("""<fcpxml version="1.13">""")

        // Resources
        xml.appendLine("""  <resources>""")
        xml.appendLine(
            """    <format id="$formatId" name="FFVideoFormat1080p${fps.toInt()}" frameDuration="$frameDuration" width="1920" height="1080" colorSpace="1-1-1 (Rec. 709)"/>"""
        )
        if (audioFilename != null) {
            xml.appendLine(
                """    <asset id="$assetId" name="$audioFilename" start="0s" duration="${
                    roundToFrame(
                        totalDuration,
                        fps
                    )
                }" hasVideo="0" hasAudio="1" format="$formatId">"""
            )
            xml.appendLine("""      <media-rep kind="original-media" src="file://localhost/$audioFilename"/>""")
            xml.appendLine("""    </asset>""")
        }
        xml.appendLine("""    <effect id="$EFFECT_ID" name="$EFFECT_NAME" uid="$EFFECT_UID"/>""")
        xml.appendLine("""  </resources>""")

        // Library and project
        xml.appendLine("""  <library>""")
        xml.appendLine("""    <event name="SongML">""")
        xml.appendLine("""      <project name="Generated Song Overlay">""")
        xml.appendLine(
            """        <sequence duration="${
                roundToFrame(
                    totalDuration,
                    fps
                )
            }" format="$formatId" tcStart="0s" tcFormat="NDF">"""
        )
        xml.appendLine("""          <spine>""")

        // Audio
        if (audioFilename != null) {
            xml.appendLine(
                """            <asset-clip name="$audioFilename" offset="0s" ref="$assetId" duration="${
                    roundToFrame(
                        totalDuration,
                        fps
                    )
                }" start="0s"/>"""
            )
        }

        // Sections
        for ((i, section) in song.sections.withIndex()) {
            val next = song.sections.getOrNull(i + 1)
            val sectionStart = roundToFrame(section.startTimeSeconds, fps)
            val sectionDuration = maxOf(0.5, section.bars * barDuration)

            // Section Title (lane 0)
            xml.appendLine(makeTitle(section.name, sectionStart, sectionDuration, lane = 0, fps))

            // Chords (lane 1)
            var currentOffset = section.startTimeSeconds
            section.lines.forEach { line ->
                if (line is ParsedLine.ChordLine) {
                    val chords = line.tokens.filterIsInstance<Token.Chord>()
                    val chordDuration = if (chords.isNotEmpty()) barDuration / chords.size else barDuration
                    chords.forEach { chord ->
                        val offsetStr = roundToFrame(currentOffset, fps)
                        xml.appendLine(makeTitle(chord.name, offsetStr, chordDuration, lane = 1, fps))
                        currentOffset += chordDuration
                    }
                }
            }

            // Next section preview (lane 2)
            next?.let {
                val previewOffset = maxOf(section.startTimeSeconds + sectionDuration - 1.5, 0.0)
                val previewTime = roundToFrame(previewOffset, fps)
                xml.appendLine(makeTitle("Next: ${it.name}", previewTime, 1.5, lane = 2, fps))
            }
        }

        // Close XML
        xml.appendLine("""          </spine>""")
        xml.appendLine("""        </sequence>""")
        xml.appendLine("""      </project>""")
        xml.appendLine("""    </event>""")
        xml.appendLine("""  </library>""")
        xml.appendLine("""</fcpxml>""")

        return xml.toString()
    }

    private fun makeTitle(text: String, offset: String, duration: Double, lane: Int, fps: Double): String {
        val clampedDuration = maxOf(0.5, duration)
        val durStr = roundToFrame(clampedDuration, fps)

        val styleId = "ts" + (text + offset + lane).hashCode().toUInt().toString(16)

        return """
        <title name="$text" lane="$lane" offset="$offset" duration="$durStr" ref="$EFFECT_ID">
          <text>
            <text-style ref="$styleId">$text</text-style>
          </text>
          <text-style-def id="$styleId">
            <text-style font="Helvetica Neue" fontSize="96" fontColor="1 1 1 1" bold="1" alignment="center"/>
          </text-style-def>
        </title>
    """.trimIndent()
    }

    private fun frameDurationString(fps: Double): String {
        val numerator = 100
        val denominator = (fps * numerator).toInt()
        return "$numerator/$denominator" + "s"
    }

    private fun roundToFrame(seconds: Double, fps: Double): String {
        val frame = 1.0 / fps
        val rounded = (seconds / frame).roundToInt() * frame
        return "%.3fs".format(rounded)
    }
}

fun getBarDuration(song: Song): Double {
    val tempo = song.tempo ?: TempoInfo()
    val beatsPerBar = tempo.timeSignature.split("/").firstOrNull()?.toIntOrNull() ?: 4
    return beatsPerBar * (60.0 / tempo.bpm)
}

