# 🎵 songml

**songml** is a Kotlin-based parser for a custom music markup format – designed for songwriters, live looping artists, and creators producing lyric/chord videos.

It parses structured song files with sections, chords, lyrics, repeats, and timing cues, and can generate **Final Cut Pro XML overlays** or **HTML lyric/chord sheets** for visual editing and performance.

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
- Generates:
  - 🎮 FCPXML overlays for Final Cut Pro
  - 📜 HTML lead sheets with aligned chords and lyrics
- Runnable via CLI or programmatically using subcommands

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

## 📆 Example Output

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
java -jar build/libs/songml-all.jar fcpxml --input song.txt
```

Optionally specify output path:

```bash
java -jar build/libs/songml-all.jar fcpxml --input song.txt --output song.fcpxml
```

You can also generate HTML:

```bash
java -jar build/libs/songml-all.jar html --input song.txt
```

You can generate from a directory of `.txt` SongML files:

```bash
songml fcpxml --input ./songs --output ./output
songml html --input ./songs --output ./output
```

---

## 🎨 Output Formats

| Command  | Description                             | Output File       |
|----------|-----------------------------------------|-------------------|
| `fcpxml` | Final Cut Pro overlay titles             | `.fcpxml`         |
| `html`   | Aligned chord/lyric HTML lead sheet     | `.html`           |

More formats like `.srt`, `.ass`, and `.pdf` planned in the roadmap.

---

## 🍺 Install via Homebrew

You can install `songml` with:

```bash
brew tap fluxtah/songml
brew install songml
```

Then run:

```bash
songml fcpxml --input my_song.txt
```

> 💡 Requires Java 17+. You can install it with:
> ```bash
> brew install openjdk
> ```

---

## 🤝 Contribute

PRs and tune contributions welcome.  
One love. One format. Many songs. 🎶
