package com.fluxtah.songml

import com.fluxtah.songml.generators.fcpxml.FCPXMLGenerator
import com.fluxtah.songml.generators.html.HtmlGenerator
import com.fluxtah.songml.parser.SongParser
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.double
import java.io.File

fun main(args: Array<String>) = SongML()
    .subcommands(SongML())
    .main(args)

fun discoverSongFiles(inputFolder: File): List<File> =
    inputFolder.walkTopDown().filter { it.isFile && it.extension == "txt" }.toList()

class SongML : CliktCommand(name = "songml", help = "Generate formatted outputs from SongML.") {
    override fun run() {}

    init {
        subcommands(
            GenerateFcpXmlCommand(),
            GenerateHtmlCommand()
            // ⬅️ Add new generators here
        )
    }
}

class GenerateFcpXmlCommand : CliktCommand(name = "fcpxml", help = "Generate Final Cut Pro XML from SongML") {
    private val input: String by option("--input", help = "Path to SongML file or directory").required()
    private val output: String? by option("--output", help = "Output file or directory")
    private val fps: Double by option("--fps", help = "Frames per second").double().default(30.0)
    private val includeLyrics: Boolean by option("--include-lyrics").flag(default = true)
    private val includeChords: Boolean by option("--include-chords").flag(default = true)

    override fun run() {
        val inputFile = File(input)
        val outputDir = output?.let { File(it) }

        val files = if (inputFile.isDirectory) {
            discoverSongFiles(inputFile)
        } else listOf(inputFile)

        files.forEach { file ->
            val song = SongParser.parse(file.readLines())
            val audioFile = file.path.replace(".txt", ".mp3")
            val fcpxml = FCPXMLGenerator(song, audioFile, fps, includeLyrics, includeChords).generate()
            val outFile = outputDir?.resolve(file.nameWithoutExtension + ".fcpxml")
                ?: File(file.path.replace(".txt", ".fcpxml"))

            ensureDirExists(outputDir)
            outFile.writeText(fcpxml)
            echo("✅ FCPXML written to ${outFile.absolutePath}")
        }
    }
}

class GenerateHtmlCommand : CliktCommand(name = "html", help = "Generate HTML from SongML") {
    private val input: String by option("--input", help = "Path to SongML file or directory").required()
    private val output: String? by option("--output", help = "Output file or directory")
    private val condensed: Boolean by option("--condensed").flag(default = true)

    override fun run() {
        val inputFile = File(input)
        val outputDir = output?.let { File(it) }

        val files = if (inputFile.isDirectory) {
            discoverSongFiles(inputFile)
        } else listOf(inputFile)

        files.forEach { file ->
            val song = SongParser.parse(file.readLines())
            val html = HtmlGenerator(song, condensed).generate()
            val outFile = outputDir?.resolve(file.nameWithoutExtension + ".html")
                ?: File(file.path.replace(".txt", ".html"))

            ensureDirExists(outputDir)
            outFile.writeText(html)
            echo("✅ HTML written to ${outFile.absolutePath}")
        }
    }
}

fun ensureDirExists(dir: File?) {
    if (dir != null && !dir.exists()) {
        dir.mkdirs()
    }
}
