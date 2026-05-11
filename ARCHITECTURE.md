# Architektur - FastPreview

FastPreview ist ein reiner Rendering‑Layer innerhalb des FastJava‑Ökosystems.

## Pipeline
1. **Content Input**: PDF, HTML, Code, Text, Markdown, SVG, Screenshot.
2. **Layout**: Backend‑spezifische Aufbereitung (z.B. WebView2 Layout-Engine, DirectWrite Glyph-Runs).
3. **Rasterization**: Erzeugung der Pixel (z.B. PDFium Renderer, Skia Surface).
4. **PixelBuffer**: Speicherung in off‑heap Speicher (BGRA).
5. **Übergabe an FastImage**: Zero‑copy Übergabe der Pixeldaten.

## Modulgrenzen
- **FastPreview**: Erzeugt nur Pixel (reine Rasterization).
- **FastImage**: Hält die Pixeldaten (off-heap).
- **FastThumb**: Erzeugt Thumbnails (skalierte Versionen).
- **FastUI**: Rendert das User Interface.
- **FastTween**: Animiert Pixelübergänge.
- **FastTheme**: Liefert Farbschemata und Styles.

## Backends
- **PDF**: PDFium (C++ API via JNI)
- **HTML**: WebView2 Off‑Screen (Chromium via JNI)
- **Code**: Syntax‑Highlighter + FastTheme
- **Text**: DirectWrite (Windows native) / FreeType (Cross-platform)
- **SVG**: Skia (High-performance vector rendering)
- **Screenshot**: OS-spezifische Capture-APIs

## Performance-Ziele
- **Deterministisch**: Minimale Varianz in der Renderzeit.
- **Zero-Copy**: Vermeidung von Speicherumkopierungen zwischen Backend und Java.
- **Parallelisierbar**: Render-Aufgaben können auf mehreren Kernen gleichzeitig laufen.
