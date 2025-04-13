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

## 🛠️ Usage (CLI from JAR)

Build the fat JAR:

```bash
./gradlew shadowJar
```

Then run:

```bash
java -jar build/libs/songml-all.jar --input royal_soldier.txt --format fcpxml
```

You can also provide an output filename:

```bash
java -jar build/libs/songml-all.jar --input song.txt --format fcpxml --output song.fcpxml
```

This will generate a Final Cut Pro XML file with all chords and section markers.

## 🔮 Roadmap Ideas

- Export to `.srt`, `.ass`, `.html` lyric formats
- Chord sheet visual renderer
- Web-based editor for SongML
- Tempo-aware looping/clip generator

## 🤝 Contribute

Open to ideas, PRs, and tune contributions.  
One love, one structure, many songs. 🎶
```
