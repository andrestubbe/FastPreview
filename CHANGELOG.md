# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Added
- Initial project structure following FastJava Blueprint
- API Skeleton for PixelBuffer and Rendering Requests
- Demo suite with DemoPDF, DemoHTML, and DemoCode examples
- Native build scripts and directory structure
- English documentation and Architecture overview
- Backend specifications (PDFium, WebView2, Skia, DirectWrite)

### Planned
- PDF (PDFium) integration: Full JNI binding for page counting, sizing, and off-heap rasterization
- HTML (WebView2) integration: Headless WebView2 integration for deterministic HTML-to-Image capture
- SVG (Skia) integration: High-performance vector rendering via Skia JNI bindings
- Text (DirectWrite) integration: Windows-native text layout and glyph rasterization
- FastTokenizer Integration: Switch from internal mock to standalone FastTokenizer module
- Syntax Layout Engine: Efficient line-breaking and tab-width management for code previews
- Markdown Renderer: AST-based rendering (CommonMark) directly to pixels
- Unified Pipeline: Standardize FastPreviewSource → FastPreviewFrame → FastImage flow
- Off-Heap Caching: Deterministic frame cache for multi-page documents
- Unified Native Build: One-click compilation for Windows, Linux, macOS via CMake
- Benchmark Matrix: Automated performance bars for each backend

## [0.1.0] - 2026-05-11

### Added
- Initial project structure following FastJava Blueprint
- API Skeleton for PixelBuffer and Rendering Requests
- Demo suite with DemoPDF, DemoHTML, and DemoCode examples
- Native build scripts and directory structure
- English documentation and Architecture overview
