# Struna

**Product name:** Struna Tuner  
**Extended store name:** Struna Guitar & Bass Tuner

Struna is a small, privacy-conscious offline tuner for Android 9+ focused first on standard 6-string guitar and 4-string bass. The design is intended to support additional tunings and 7-string guitar without redesigning the pitch-detection core.

## Product principles

- Fast start and simple tuning workflow.
- Core tuning works offline.
- Small application and dependency footprint.
- English default UI with standard Android localization; Russian is the first translation.
- Microphone audio is processed locally by default.
- No shared project credentials embedded in the APK.
- Tuning definitions are data-driven and separate from pitch detection.

## Initial scope

- Android 9+ (`minSdk 28`).
- Android application ID and namespace: `com.dilukhin.struna`.
- Guitar 6 Standard: E2 A2 D3 G3 B3 E4.
- Bass 4 Standard: E1 A1 D2 G2.
- Target note/string and cents deviation.
- Explicit testing of low bass E1 and octave-confusion failure modes.

## Development workspace

The canonical repository is `https://github.com/dilukhin/struna`.

Project/design documents are intentionally maintained outside this Git repository in the parent development workspace. This README contains only repository-facing product context; implementation status is determined by the current code, tests and build configuration.
