package com.fluxtah.songml.parser

import com.fluxtah.songml.model.ParsedLine
import com.fluxtah.songml.model.Token

object LineParser {
    fun parse(line: String): ParsedLine {
        val cleanLine = line.trim()
        if (cleanLine.isEmpty()) return ParsedLine.TextLine(emptyList())

        val tokens = Tokenizer(cleanLine).tokenize()
        val isChordLine = tokens.none { it is Token.Word }
        return if (isChordLine) ParsedLine.ChordLine(tokens) else ParsedLine.TextLine(tokens)
    }
}