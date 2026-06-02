# Roadmap - FastPreview v0.1.0 (Next Steps)

This roadmap outlines the planned evolution of FastPreview. While we remain on version **0.1.0** for stability, these internal milestones define the path toward full native integration.

## 1. Native Backend Integration
- [ ] **PDF (PDFium)**: Full JNI binding for page counting, sizing, and off-heap rasterization (ARGB/BGRA).
- [ ] **HTML (WebView2)**: Headless WebView2 integration for deterministic HTML-to-Image capture.
- [ ] **SVG (Skia)**: High-performance vector rendering via Skia JNI bindings.
- [ ] **Text (DirectWrite)**: Windows-native text layout and glyph rasterization.

## 2. Code & Language Support
- [ ] **FastTokenizer Integration**: Switch from internal mock to standalone `FastTokenizer` module.
- [ ] **Syntax Layout Engine**: Efficient line-breaking and tab-width management for code previews.
- [ ] **Markdown Renderer**: AST-based rendering (CommonMark) directly to pixels without a browser.

## 3. Architecture & Pipeline
- [ ] **Unified Pipeline**: Standardize `FastPreviewSource → FastPreviewFrame → FastImage` flow across all backends.
- [ ] **Off-Heap Caching**: Implement a deterministic frame cache for multi-page documents.
- [ ] **Emoji Handling Policy**: Define and implement a minimalist strategy for Unicode symbols (prioritizing precision over "decoration").

## 4. Build System & Tools
- [ ] **Unified Native Build**: One-click compilation for Windows (MSVC), Linux (GCC), and macOS (Clang) via CMake.
- [ ] **Benchmark Matrix**: Automated performance bars for each backend (Render time vs. Memory pressure).

## 5. Demo Expansion
- [ ] Comprehensive Demo Suite for all backends.
- [ ] Real-world usage examples (File Explorer Integration, IDE Preview).

---
*Note: This roadmap is dynamic and follows the FastJava philosophy of aggressive development with stable versioning.*
