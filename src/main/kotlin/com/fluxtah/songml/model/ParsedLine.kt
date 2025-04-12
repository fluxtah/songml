package com.fluxtah.songml.model

import com.fluxtah.songml.model.Token

sealed class ParsedLine {
    data class ChordLine(val tokens: List<Token>) : ParsedLine()
    data class TextLine(val tokens: List<Token>) : ParsedLine()
}