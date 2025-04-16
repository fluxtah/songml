package com.fluxtah.songml.generators.html


import com.fluxtah.songml.generators.Generator
import com.fluxtah.songml.model.ParsedLine
import com.fluxtah.songml.model.Song
import com.fluxtah.songml.model.Token

class HtmlGenerator(private val song: Song, private val condensed: Boolean = true) : Generator {
    override fun generate(): String {
        val title: String? = song.extra["Title"] ?: "SongML Export"

        return buildString {
            append(
                """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>$title</title>
                    <style>
                        body { font-family: sans-serif; line-height: 1.6; background: #fafafa; padding: 2em; }
                        h2 { background: #e0e0e0; padding: 0.5em; border-radius: 4px; }
                        .line-pair { margin-bottom: 1em; }
                        .chordline { font-family: monospace; color: #0066cc; font-weight: bold; white-space: pre; }
                        .textline { font-family: monospace; white-space: pre; }
                        .hold { color: #999; }
                        .rest { color: #cc0000; font-style: italic; }
                        .ellipsis { font-style: italic; color: #999; }
                    </style>
                </head>
                <body>
                <h1>$title</h1>
            """.trimIndent()
            )

            for (section in song.sections) {
                append("<h2>[${section.name} ${section.bars} bars]</h2>\n")

                val linesToRender = if (condensed) {
                    listOfNotNull(section.lines.firstOrNull { it is ParsedLine.TextLine })
                } else {
                    section.lines
                }

                for (line in linesToRender) {
                    when (line) {
                        is ParsedLine.ChordLine -> {
                            append("<div class=\"line-pair\">")
                            append("<div class=\"chordline\">")
                            line.tokens.forEach { append(renderToken(it, true)).append(" ") }
                            append("</div><div class=\"textline\">\u00A0</div></div>\n")
                        }

                        is ParsedLine.TextLine -> {
                            val chords = StringBuilder()
                            val lyrics = StringBuilder()

                            val tokenPairs = line.tokens.map { token ->
                                when (token) {
                                    is Token.Chord -> Pair(token.name, "")
                                    is Token.Word -> Pair("", token.text)
                                    Token.Hold -> Pair("..", "")
                                    Token.Rest -> Pair("REST", "")
                                    Token.OpenParen -> Pair("(", "(")
                                    Token.CloseParen -> Pair(")", ")")
                                    is Token.Repeat -> Pair("x${token.count}", "")
                                }
                            }

                            tokenPairs.forEach { (chord, word) ->
                                val width = maxOf(chord.length, word.length, 4)
                                chords.append(chord.padEnd(width)).append(" ")
                                lyrics.append(word.padEnd(width)).append(" ")
                            }

                            append("<div class=\"line-pair\">\n")
                            append("<div class=\"chordline\">${chords.toString().trim()}</div>\n")
                            append("<div class=\"textline\">${lyrics.toString().trim()}</div>\n")
                            append("</div>\n")

                            if (condensed) {
                                append("<div class=\"ellipsis\">...</div>\n")
                            }
                        }
                    }
                }
            }

            append(
                """
                </body>
                </html>
            """
            )
        }
    }

    private fun renderToken(token: Token, isChordOnly: Boolean = false): String {
        return when (token) {
            is Token.Chord -> token.name
            is Token.Word -> if (isChordOnly) "" else token.text
            Token.Hold -> ".."
            Token.Rest -> "REST"
            Token.OpenParen -> "("
            Token.CloseParen -> ")"
            is Token.Repeat -> "x${token.count}"
        }
    }
}
