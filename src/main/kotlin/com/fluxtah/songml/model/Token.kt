package com.fluxtah.songml.model

sealed class Token {
    data class Chord(val name: String) : Token()
    data class Word(val text: String) : Token()
    object Hold : Token()  // For ".."
    object Rest : Token()  // For {--} or {REST}
    object OpenParen : Token()  // For "("
    object CloseParen : Token() // For ")"
    data class Repeat(val count: Int) : Token() // For xN
}