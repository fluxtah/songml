# 🎵 songml

**songml** is a Kotlin-based parser for a custom music markup format – designed for songwriters, live looping artists, and creators producing lyric/chord videos.

It parses structured song files with sections, chords, lyrics, repeats, and timing cues, and can generate **Final Cut Pro XML overlays** for video editing.

---

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
- Runnable via CLI or programmatically

---

## 🎤 Example Input (SongML)

```text
[Intro 4]
({D} .. {Am} .. {G} .. {D} {G}) x2

[Verse 8]
Mi fada used to {D}chant pon da {Am}microphone
{D}Left, right and left and a {G}right
```

---

## 📦 Example Output

```
[Verse 8 bars]
  TextLine: Mi fada used to {D} chant pon da {Am} microphone
  TextLine: {D} Left, right and left and a {G} right
```

---

## 📘 SongML Format Reference

Curious about the full grammar and syntax of SongML?

👉 [Read the SongML Format Specification](./docs/songml-format.md)

Covers chords, lyrics, repeats, section headers, metadata, and more.

---

## 🛠️ Usage (CLI from JAR)

### 🧪 Run locally (for devs):

```bash
./gradlew shadowJar
java -jar build/libs/songml-all.jar --input song.txt --format fcpxml
```

Optionally specify output path:

```bash
java -jar build/libs/songml-all.jar --input song.txt --format fcpxml --output song.fcpxml
```

---

## 🍺 Install via Homebrew

You can install `songml` with:

```bash
brew tap fluxtah/songml
brew install songml
```

Then run:

```bash
songml --input my_song.txt --format fcpxml
```

> 💡 Requires Java 17+. You can install it with:
> ```bash
> brew install openjdk
> ```

---

## 🔮 Roadmap Ideas

- Export to `.srt`, `.ass`, `.html` lyric formats
- Visual chord sheet renderer
- Web-based editor for SongML
- Tempo-aware looping/clip generator
- Integration with live loopers (MIDI / OSC?)

---

## 🤝 Contribute

PRs and tune contributions welcome.  
One love. One format. Many songs. 🎶
