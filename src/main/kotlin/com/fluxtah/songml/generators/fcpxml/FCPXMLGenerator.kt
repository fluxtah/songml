package com.fluxtah.songml.generators.fcpxml

import com.fluxtah.songml.generators.Generator
import com.fluxtah.songml.model.ParsedLine
import com.fluxtah.songml.model.Song
import com.fluxtah.songml.model.TempoInfo
import com.fluxtah.songml.model.Token
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.XmlVersion
import org.redundent.kotlin.xml.xml
import kotlin.math.roundToInt

class FCPXMLGenerator(
    val song: Song,
    val audioFilename: String? = null,
    val fps: Double = 30.0,
    val includeLyrics: Boolean = true,
    val includeChords: Boolean = true
) : Generator {

    val barDuration = getBarDuration(song)
    val totalDuration = song.totalBars * barDuration
    val frameDuration = frameDurationString(fps)
    val formatId = FORMAT_ID
    val assetId = ASSET_ID

    override fun generate(): String {
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

                                song.sections.forEachIndexed { sectionIndex, section ->
                                    val sectionStart = roundToFrame(section.startTimeSeconds, fps)
                                    val sectionDuration = maxOf(0.5, section.bars * barDuration)
                                    val durStr = roundToFrame(sectionDuration, fps)

                                    val sectionStyleId = "ts_sec$sectionIndex"
                                    val contentStyleId = "cs_sec$sectionIndex"

                                    title(section.name, sectionStart, durStr, lane = 0) {
                                        backgroundOpacity()
                                        alignTopCenter()
                                        text {
                                            textStyle(sectionStyleId, section.name)
                                        }
                                        textStyleDef(sectionStyleId) {
                                            textStyleDefTextStyle(fontSize = "96")
                                        }

                                        // CONTENT block: lyrics + chords together
                                        val combinedContent = section.lines.joinToString("\n") { line ->
                                            lineText(line)
                                        }.trim()

                                        title(combinedContent, "0s", durStr, lane = 1) {
                                            backgroundOpacity()
                                            text {
                                                textStyle(contentStyleId, combinedContent)
                                            }
                                            textStyleDef(contentStyleId) {
                                                textStyleDefTextStyle(fontSize = "48")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.toString(true)
    }


    private fun lineText(line: ParsedLine): String {
        return when (line) {
            is ParsedLine.TextLine -> line.tokens.joinToString(" ") {
                when (it) {
                    is Token.Chord -> "[${it.name}]"
                    is Token.Word -> it.text
                    Token.Hold -> ".."
                    Token.Rest -> "REST"
                    is Token.Repeat -> "x${it.count}"
                    Token.OpenParen -> "("
                    Token.CloseParen -> ")"
                }
            }.trim()

            is ParsedLine.ChordLine -> line.tokens.joinToString(" ") {
                when (it) {
                    is Token.Chord -> it.name
                    Token.Hold -> ".."
                    Token.Rest -> "REST"
                    is Token.Repeat -> "x${it.count}"
                    Token.OpenParen -> "("
                    Token.CloseParen -> ")"
                    else -> ""
                }
            }.trim()
        }
    }

    private fun Node.textStyleDefTextStyle(fontSize: String = "96") {
        "text-style" {
            attribute("font", "Helvetica Neue")
            attribute("fontSize", fontSize)
            attribute("fontColor", "1 1 1 1")
            attribute("bold", "1")
            attribute("alignment", "center")
        }
    }

    private fun Node.textStyle(styleId: String, titleText: String) {
        "text-style" {
            attribute("ref", styleId)
            text(titleText)
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
                attribute("lane", "-1")
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

    private fun Node.param(name: String, key: String, value: String) {
        "param" {
            attribute("name", name)
            attribute("key", key)
            attribute("value", value)
        }
    }

    private fun Node.layoutMethodParagraph() =
        param("Layout Method", "9999/10003/13260/3296675261/2/314", "1 (Paragraph)")

    private fun Node.leftRightMargins(left: String = "-1730", right: String = "1730") {
        param("Left Margin", "9999/10003/13260/3296675261/2/323", left)
        param("Right Margin", "9999/10003/13260/3296675261/2/324", right)
    }

    private fun Node.topBottomMargins(top: String = "960", bottom: String = "-960") {
        param("Top Margin", "9999/10003/13260/3296675261/2/325", top)
        param("Bottom Margin", "9999/10003/13260/3296675261/2/326", bottom)
    }

    private fun Node.autoShrinkToMargins() =
        param("Auto-Shrink", "9999/10003/13260/3296675261/2/370", "3 (To All Margins)")

    private fun Node.alignBlockCenter() =
        param("Alignment", "9999/10003/13260/3296675261/2/354/3296667395/401", "1 (Center)")

    private fun Node.alignTopLeft() =
        param("Alignment", "9999/10003/13260/3296675261/2/373", "0 (Left) 0 (Top)")

    private fun Node.alignTopCenter() =
        param("Alignment", "9999/10003/13260/3296675261/2/373", "1 (Center) 0 (Top)")

    private fun Node.alignMiddleLeft() =
        param("Alignment", "9999/10003/13260/3296675261/2/373", "0 (Left) 1 (Middle)")

    private fun Node.backgroundOpacity(value: String = "0") =
        param("Background Opacity", "9999/10003/3296541111/3296541115/1/200/202", value)


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
            attribute("name", song.titleOrFallback())
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

    private fun Node.title(
        titleText: String,
        sectionStart: String,
        durStr: String,
        lane: Int,
        block: Node.() -> Unit = {}
    ) {
        "title" {
            attribute("name", titleText)
            attribute("lane", lane.toString())
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
    val frames = (seconds * fps).roundToInt()
    val denom = fps.toInt()
    return "$frames/$denom" + "s"
}

fun getBarDuration(song: Song): Double {
    val tempo = song.tempo ?: TempoInfo()
    val beatsPerBar = tempo.timeSignature.split("/").firstOrNull()?.toIntOrNull() ?: 4
    return beatsPerBar * (60.0 / tempo.bpm)
}

fun Song.titleOrFallback(): String = this.extra["Title"] ?: "Untitled Song"
