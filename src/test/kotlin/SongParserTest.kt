package com.fluxtah.songml.parser

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.Test

class SongParserTest {

    @Test
    fun testParsesTempoAndSectionsCorrectly() {
        val input = """
            [Tempo]
            BPM: 82
            TimeSig: 4/4
            
            [Lead In 4]
            [Intro 4]
            {D} {Am} {G} {D}
            
            [Verse 8]
            Line one with {D}
            Line two with {Am}
            
            [Outro 2]
            {D}
        )
        """.trimIndent().lines()

        val song = SongParser.parse(input)

        val barDuration = 60.0 / 82.0 * 4
        val expectedTotalBars = 4 + 4 + 8 + 2
        val expectedDuration = expectedTotalBars * barDuration

        assertEquals(expectedTotalBars, song.totalBars, "Total bars mismatch")
        assertNotNull(song.tempo, "Tempo should not be null")
        assertEquals(82.0, song.tempo.bpm, "BPM mismatch")
        assertEquals("4/4", song.tempo.timeSignature, "Time signature mismatch")
        assertEquals(expectedDuration, song.totalBars * barDuration, 0.001, "Duration mismatch")

        val sectionNames = song.sections.map { it.name }
        assertEquals(listOf("Lead In", "Intro", "Verse", "Outro"), sectionNames, "Section names mismatch")

        val sectionBars = song.sections.map { it.bars }
        assertEquals(listOf(4, 4, 8, 2), sectionBars, "Section bars mismatch")

        val startBars = song.sections.map { it.startBar }
        assertEquals(listOf(0, 4, 8, 16), startBars, "Section start bars mismatch")
    }
}
