package com.fluxtah.songml

import com.fluxtah.songml.parser.SongParser
import com.fluxtah.songml.generators.fcpxml.FCPXMLGenerator
import com.fluxtah.songml.generators.html.HtmlGenerator
import com.fluxtah.songml.model.Song
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
            generateFcpXml(input, song, output)
        }

        "html" -> {
            generateHtml(input, song, output)
        }

        else -> {
            println("❌ Unsupported format: $format")
        }
    }
}

private fun generateHtml(input: String, song: Song, output: String?) {
    val html = HtmlGenerator(song).generate()
    val outFile = output ?: input.replace(".txt", ".html")
    File(outFile).writeText(html)
    println("✅ HTML written to $outFile")
}

private fun generateFcpXml(input: String, song: Song, output: String?) {
    val audioFile = input.replace(".txt", ".mp3") // placeholder logic
    val fcpxml = FCPXMLGenerator(song, audioFile).generate()
    val outFile = output ?: input.replace(".txt", ".fcpxml")
    File(outFile).writeText(fcpxml)
    println("✅ FCPXML written to $outFile")
}
