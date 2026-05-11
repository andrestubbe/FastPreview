# FastPreview

High-performance rendering layer for PDF, HTML, Code, Text, Markdown, SVG, and screenshots.

Erzeugt off-heap Pixel für FastImage. Keine UI. Keine Animation. Reine Rasterization.

## Status
Modularer Kern + Backend-Definitionen stabil.  
Backends werden schrittweise integriert (PDFium, WebView2, Skia, DirectWrite).

## Zielsetzung
Ein universeller, schneller Rendering-Baustein für das Fast-Ökosystem:
- File-Explorer Previews
- IDE/Editor Previews
- Batch-Rendering
- Pipeline-Demos
- Benchmark-Vergleiche

## Architektur
Content → Layout → Rasterization → PixelBuffer → FastImage  

Strikte Modulgrenzen, zero-copy, off-heap.

## Lizenz
MIT
