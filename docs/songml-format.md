# 📝 SongML Format Specification

**SongML** is a plain-text, human-readable markup language for writing musical song structures that include chords, lyrics, timing, and cues for audio-visual syncing. It’s designed to be easy for musicians to write by hand and powerful enough for automated tools like `songml` to parse and process.

---

## 🔤 Basic Structure

A SongML file is divided into sections, each with a label and optional bar count.

```text
[Intro 4]
...
[Verse 8]
...
[Chorus 4]
...
```

Each section contains **lines**, which can be:
- A **ChordLine** (pure chord/bar structure)
- A **TextLine** (lyrics with chords inline)

---

## 🎶 ChordLine Syntax

A line that contains only chords and timing symbols. These represent one bar per chord (or hold):

```text
{D} .. {Am} .. {G} .. {D} {G}
```

### Special Tokens:
- `{CHORD}` – a chord (e.g., `{Am}`, `{G#}`, `{Cmaj7}`)
- `..` – a hold or continuation (no new chord, same as previous)
- `{REST}` or `{--}` – rest bar (silence)

### Repetition:
Use `( ... ) xN` to repeat a chord pattern:
```text
({D} {Am}) x4
```

---

## 🎤 TextLine Syntax

Lyrics with inline chord cues. Chords must be wrapped in `{}` and appear inline with lyrics:

```text
Mi fada used to {D}chant pon da {Am}microphone
{D}Left, right and left and a {G}right
```

These lines are parsed for token alignment and syncing.

---

## 🪘 Section Header

A section starts with a label in brackets:
```text
[Verse 8]
```
- `Verse`: the name of the section
- `8`: number of bars (optional, can be 0)

Section names can be anything: `Intro`, `Bridge`, `Solo`, `Break`, etc.

---

## 🎚 Metadata / Extras

Anything not recognized as a section, chord line, or text line is stored as a key-value metadata line. These appear at the end or in a `[Meta]` section:

```text
Key: G
Tempo: 82
TimeSig: 4/4
Audio: my_song.mp3
```

These are used for syncing and exporting formats like FCPXML.

---

## 🧪 Example

```text
[Intro 4]
({D} .. {Am} .. {G} .. {D} {G}) x2

[Verse 8]
Mi fada used to {D}chant pon da {Am}microphone
{D}Left, right and left and a {G}right

[Chorus 8]
Mi fada was a {D}soldier
For the {Am}queen of England
```

---

## ✅ Tips
- Always use `{}` around chords
- Use consistent spacing — no tabs
- Bar counts help with timing/exporting but aren't required
- Repetition makes chord patterns concise
