package com.fluxtah.songml

import com.fluxtah.songml.parser.SongParser
import com.fluxtah.songml.writer.fcpxml.FCPXMLGenerator
import java.io.File

fun main(args: Array<String>) {
    val argMap = args
        .toList()
        .chunked(2)
        .filter { it.size == 2 }
        .associate { it[0] to it[1] }

    val input = argMap["--input"]
    val format = argMap["--format"] ?: "fcpxml"
    val output = argMap["--output"]

    if (input == null) {
        println("❌ Error: --input is required.")
        println("Usage: songml --input file.txt [--format fcpxml] [--output file.fcpxml]")
        return
    }

    val song = SongParser.parse(File(input).readLines())

    when (format.lowercase()) {
        "fcpxml" -> {
            val audioFile = input.replace(".txt", ".mp3") // placeholder logic
            val fcpxml = FCPXMLGenerator(song, audioFile).generate()
            val outFile = output ?: input.replace(".txt", ".fcpxml")
            File(outFile).writeText(fcpxml)
            println("✅ FCPXML written to $outFile")
        }
        else -> {
            println("❌ Unsupported format: $format")
        }
    }
}

/**
fun testGenerateFCPXML() {
    val songLines = File("royal_soldier.txt").readLines()
    val song = SongParser.parse(songLines)

    val audioFilename = "royal_soldier.mp3" // just a placeholder name

    val fcpxml = FCPXMLGenerator(song, audioFilename).generate()
    println("🎵 Tempo: ${song.tempo}")
    println("🎵 Total Bars: ${song.totalBars}")
    println("🕒 Bar Duration: ${getBarDuration(song)}")
    println("📏 Expected Timeline Duration: ${song.totalBars * getBarDuration(song)} seconds")

    File("royal_soldier2.fcpxml").writeText(fcpxml)
    println("✅ FCPXML exported to royal_soldier.fcpxml")
}

fun main() {
    testGenerateFCPXML()
    //   com.fluxtah.songml.testParsing()
}

private fun testParsing() {
    val songLines = File("royal_soldier.txt").readLines()
    val parsedSong = SongParser.parse(songLines)

    println("Total Bars: ${parsedSong.totalBars}")
    println("Tempo Info: ${parsedSong.tempo}")
    println("Parsed Sections:")
    parsedSong.sections.forEach { section ->
        println("Section: ${section.name}")
        println("   Bars: ${section.bars}")
        println("   Start Bar: ${section.startBar}")
        println("   Start Time Seconds: ${section.startTimeSeconds}")

        section.lines.forEach { line ->
            val tokens = when (line) {
                is ParsedLine.ChordLine -> line.tokens
                is ParsedLine.TextLine -> line.tokens
            }
            print("  ${line::class.simpleName}: ")
            println(tokens.joinToString(" ") {
                when (it) {
                    is Token.Chord -> "{${it.name}}"
                    is Token.Word -> it.text
                    Token.Hold -> ".."
                    Token.Rest -> "{REST}"
                    Token.OpenParen -> "("
                    Token.CloseParen -> ")"
                    is Token.Repeat -> "x${it.count}"
                }
            })
        }
    }

    println("Extras:")
    parsedSong.extra.forEach { line ->
        println("${line.key}: ${line.value}")
    }
}
*/