package com.fluxtah.songml.writer.fcpxml

import com.fluxtah.songml.model.Song
import com.fluxtah.songml.model.TempoInfo
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.XmlVersion
import org.redundent.kotlin.xml.xml
import kotlin.math.roundToInt

class FCPXMLGenerator(val song: Song, val audioFilename: String? = null, val fps: Double = 30.0) {

    val barDuration = getBarDuration(song)
    val totalDuration = song.totalBars * barDuration
    val frameDuration = frameDurationString(fps)
    val formatId = FORMAT_ID
    val assetId = ASSET_ID

    fun generate(): String {
        return xml("fcpxml", version = XmlVersion.V10, encoding = "UTF-8") {
            attribute("version", "1.13")
            doctype("fcpxml")

            resources {
                format()
                audioFilename?.let { name ->
                    asset(name) {
                        mediaRep(name)
                    }
                }
                effect()
            }

            library {
                event {
                    project {
                        sequence {
                            spine {
                                assetClip()

                                song.sections.forEachIndexed { index, section ->
                                    val next = song.sections.getOrNull(index + 1)
                                    val sectionStart = roundToFrame(section.startTimeSeconds, fps)
                                    val sectionDuration = maxOf(0.5, section.bars * barDuration)

                                    // Section title (lane 0)
                                    val titleText = section.name
                                    val styleId = "ts" + (titleText + sectionStart + 0).hashCode().toUInt().toString(16)
                                    val durStr = roundToFrame(sectionDuration, fps)

                                    title(titleText, sectionStart, durStr) {
                                        text {
                                            textStyle(styleId, titleText)
                                        }
                                        textStyleDef(styleId) {
                                            textStyleDefTextStyle()
                                        }
                                    }
                                    // Add chords and next preview the same way...
                                }
                            }
                        }
                    }
                }
            }
        }.toString(true)
    }

    private fun Node.textStyleDefTextStyle() {
        "text-style" {
            attribute("font", "Helvetica Neue")
            attribute("fontSize", "96")
            attribute("fontColor", "1 1 1 1")
            attribute("bold", "1")
            attribute("alignment", "center")
        }
    }

    private fun Node.textStyle(styleId: String, titleText: String) {
        "text-style" {
            attribute("ref", styleId)
            -titleText
        }
    }

    private fun Node.assetClip() {
        audioFilename?.let { name ->
            "asset-clip" {
                attribute("name", name)
                attribute("offset", "0s")
                attribute("ref", assetId)
                attribute("duration", roundToFrame(totalDuration, fps))
                attribute("start", "0s")
            }
        }
    }

    private fun Node.mediaRep(name: String) {
        "media-rep" {
            attribute("kind", "original-media")
            attribute("src", "file://localhost/$name")
        }
    }

    private fun Node.effect() {
        "effect" {
            attribute("id", EFFECT_ID)
            attribute("name", EFFECT_NAME)
            attribute("uid", EFFECT_UID)
        }
    }

    private fun Node.asset(name: String, block: Node.() -> Unit = {}): Node = "asset" {
        attribute("id", assetId)
        attribute("name", name)
        attribute("start", "0s")
        attribute("duration", roundToFrame(totalDuration, fps))
        attribute("hasVideo", "0")
        attribute("hasAudio", "1")
        attribute("format", formatId)
        block()
    }

    private fun Node.format() {
        "format" {
            attribute("id", formatId)
            attribute("name", "FFVideoFormat1080p${fps.toInt()}")
            attribute("frameDuration", frameDuration)
            attribute("width", "1920")
            attribute("height", "1080")
            attribute("colorSpace", "1-1-1 (Rec. 709)")
        }
    }

    private fun Node.library(block: Node.() -> Unit = {}) {
        "library" {
            block()
        }
    }

    private fun Node.event(block: Node.() -> Unit = {}) {
        "event" {
            attribute("name", "SongML")
            block()
        }
    }

    private fun Node.project(block: Node.() -> Unit = {}) {
        "project" {
            attribute("name", "Generated Song Overlay")
            block()
        }
    }

    private fun Node.sequence(block: Node.() -> Unit = {}) {
        "sequence" {
            attribute("duration", roundToFrame(totalDuration, fps))
            attribute("format", formatId)
            attribute("tcStart", "0s")
            attribute("tcFormat", "NDF")
            block()
        }
    }

    private fun Node.spine(block: Node.() -> Unit = {}) {
        "spine" {
            block()
        }
    }

    private fun Node.resources(block: Node.() -> Unit = {}) {
        "resources" {
            block()
        }
    }

    private fun Node.title(titleText: String, sectionStart: String, durStr: String, block: Node.() -> Unit = {}) {
        "title" {
            attribute("name", titleText)
            attribute("lane", "0")
            attribute("offset", sectionStart)
            attribute("duration", durStr)
            attribute("ref", EFFECT_ID)
            block()
        }
    }

    private fun Node.text(block: Node.() -> Unit = {}) {
        "text" {
            block()
        }
    }

    private fun Node.textStyleDef(styleId: String, block: Node.() -> Unit = {}) {
        "text-style-def" {
            attribute("id", styleId)
            block()
        }
    }
}

private const val EFFECT_ID = "r2"
private const val EFFECT_NAME = "Graphic Text"
private const val EFFECT_UID = ".../Titles.localized/Basic Text.localized/Graphic Text.localized/Graphic Text.moti"
private const val FORMAT_ID = "r1"
private const val ASSET_ID = "a1"

fun frameDurationString(fps: Double): String {
    val numerator = 100
    val denominator = (fps * numerator).toInt()
    return "$numerator/$denominator" + "s"
}

fun roundToFrame(seconds: Double, fps: Double): String {
    val frame = 1.0 / fps
    val rounded = (seconds / frame).roundToInt() * frame
    return "%.3fs".format(rounded)
}

fun getBarDuration(song: Song): Double {
    val tempo = song.tempo ?: TempoInfo()
    val beatsPerBar = tempo.timeSignature.split("/").firstOrNull()?.toIntOrNull() ?: 4
    return beatsPerBar * (60.0 / tempo.bpm)
}

