# 🎵 songml

**songml** is a Kotlin-based parser for a custom music markup format – designed for songwriters, live looping artists, and creators producing lyric/chord videos.

It parses structured song files with sections, chords, lyrics, repeats, and timing cues, and can generate **Final Cut Pro XML overlays** for video editing.

## ✨ Features

- Parses song sections like `[Verse 8]`, `[Chorus 4]`
- Supports inline chord notation:
  - `{D}`, `{Am}`, `{G#}`
  - `..` for chord holds
  - `{REST}` / `{--}` for rests
  - `( ... ) xN` for repeated patterns
- Clean lexer-style tokenizer (no regex)
- Calculates tempo, total bars, and section timings
- Generates FCPXML overlays for Final Cut Pro

## 🎤 Example Input (SongML)

```text
[Intro 4]
({D} .. {Am} .. {G} .. {D} {G}) x2

[Verse 8]
Mi fada used to {D}chant pon da {Am}microphone
{D}Left, right and left and a {G}right
```

## 📦 Example Output

```
[Verse 8 bars]
  TextLine: Mi fada used to {D} chant pon da {Am} microphone
  TextLine: {D} Left, right and left and a {G} right
```

## 🛠️ Usage (Code Example)

```kotlin
    val songLines = File("royal_soldier.txt").readLines()
    val song = SongParser.parse(songLines)

    println("Tempo: ${song.tempo}")
    println("Total Bars: ${song.totalBars}")
    println("First Section Starts At: ${song.sections.firstOrNull()?.startTimeSeconds} sec")

    val audioFilename = "royal_soldier.mp3"
    val fcpxml = FCPXMLGenerator(song, audioFilename).generate()

    File("royal_soldier.fcpxml").writeText(fcpxml)
```

## 🔮 Roadmap Ideas

- Export to `.srt`, `.ass`, `.html` lyric formats
- Chord sheet visual renderer
- Web-based editor for SongML
- Tempo-aware looping/clip generator

## 🤝 Contribute

Open to ideas, PRs, and tune contributions.  
One love, one structure, many songs. 🎶