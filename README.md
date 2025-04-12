# 🎵 songml

A Kotlin-based parser for a custom music markup format (SongML) – designed for songwriters. 

Parse structured song files with chords, lyrics, timing cues, and export-ready structure.

FCPXML generator for generating overlays in lyric/chord videos

## ✨ Features

- Parses song sections like `[Verse 8]`, `[Chorus 4]`
- Supports inline chord notation: `{D}`, `{Am}`, `{G#}`
- Recognizes:
  - `{REST}` or `{--}` for rests
  - `..` for chord holds
  - `( … ) xN` for repeated progressions

## 🎤 Example Input

```text
[Intro 4]
({D} .. {Am} .. {G} .. {D} {G}) x2

[Verse 8]
Mi fada used to {D}chant pon da {Am}microphone
{D}Left, right and left and a {G}right
```

## 📦 Output

```
[Verse 8 bars]
  TextLine: Mi fada used to {D} chant pon da {Am} microphone
  TextLine: {D} Left, right and left and a {G} right
```

## 🔮 Roadmap Ideas

- Export to `.srt`, `.fcpxml`, `.ass`, `.html`
- Visual chord chart renderer

## 🤝 Contribute

PRs welcome! Share improvements, features, or tunes.
