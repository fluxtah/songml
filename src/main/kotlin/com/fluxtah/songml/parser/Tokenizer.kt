package com.fluxtah.songml.parser

import com.fluxtah.songml.model.Token

class Tokenizer(private val input: String) {
    private var index = 0
    private val tokens = mutableListOf<Token>()

    fun tokenize(): List<Token> {
        while (index < input.length) {
            val start = index
            when (val char = input[index]) {
                '{' -> readChordOrRest()
                '.' -> readHold()
                '(' -> {
                    tokens.add(Token.OpenParen); index++
                }

                ')' -> {
                    tokens.add(Token.CloseParen); index++
                }

                'x', 'X' -> if (!readRepeat()) readWord()
                ' ', '\t' -> index++
                else -> readWord()
            }
            // Safety check: infinite loop prevention
            if (index == start) {
                println("⚠️ Tokenizer stuck at index $index: '${input.getOrNull(index)}'")
                index++ // Skip to prevent hanging
            }
        }
        return tokens
    }


    private fun readRepeat(): Boolean {
        val start = index
        if (input[start].lowercaseChar() != 'x') return false

        var i = start + 1
        while (i < input.length && input[i].isDigit()) i++

        return if (i > start + 1) {
            val count = input.substring(start + 1, i).toIntOrNull()
            if (count != null) {
                tokens.add(Token.Repeat(count))
                index = i
                true
            } else false
        } else false
    }

    private fun readChordOrRest() {
        val end = input.indexOf('}', index)
        if (end == -1) {
            index++
            return
        }
        val content = input.substring(index + 1, end).trim()
        val token = when (content.uppercase()) {
            "--", "REST" -> Token.Rest
            else -> Token.Chord(content)
        }
        tokens.add(token)
        index = end + 1
    }

    private fun readHold() {
        if (index + 1 < input.length && input[index + 1] == '.') {
            tokens.add(Token.Hold)
            index += 2
        } else {
            index++
        }
    }

    private fun readWord() {
        val start = index
        while (
            index < input.length && !input[index].isWhitespace() &&
            input[index] != '{' && input[index] != '}' &&
            input[index] != '(' && input[index] != ')'
        // ⚠️ remove x check so it’s allowed in words
        ) {
            index++
        }
        val word = input.substring(start, index)
        if (word.isNotEmpty()) tokens.add(Token.Word(word))
    }

}