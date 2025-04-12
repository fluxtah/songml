
import com.fluxtah.songml.model.ParsedLine
import com.fluxtah.songml.parser.SongParser
import com.fluxtah.songml.model.Token
import com.fluxtah.songml.writer.fcpxml.FCPXMLWriter
import com.fluxtah.songml.writer.fcpxml.getBarDuration
import java.io.File

fun testGenerateFCPXML() {
    val songLines = File("royal_soldier.txt").readLines()
    val song = SongParser.parse(songLines)

    val audioFilename = "royal_soldier.mp3" // just a placeholder name

    val fcpxml = FCPXMLWriter.generateFCPXML(song, audioFilename)
    println("🎵 Tempo: ${song.tempo}")
    println("🎵 Total Bars: ${song.totalBars}")
    println("🕒 Bar Duration: ${getBarDuration(song)}")
    println("📏 Expected Timeline Duration: ${song.totalBars * getBarDuration(song)} seconds")

    File("royal_soldier.fcpxml").writeText(fcpxml)
    println("✅ FCPXML exported to royal_soldier.fcpxml")
}
fun main() {
    testGenerateFCPXML()
  //   testParsing()
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
