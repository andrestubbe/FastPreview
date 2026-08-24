# Changelog: FastPreview

All notable changes to this project will be documented in this file.

## [0.1.0] - 2026-08-24
### Added
- **Multi-Engine Document Renderer (`FastPreview`)**: Hybrid PDFium, PDFBox 3.0, HTML, and Markdown rasterizer.
- **FastTokenize Cross-Integration**: Real-time syntax highlighting token streams for source code previews.
- **Two-Stage OS UI Pipeline (`renderWithThumbnailFallback`)**: First-stage `FastThumb` Shell cache with deep `FastPreview` fallback.
- **FastFileFormat Metadata Streamer (`PreviewCodec`)**: Compact `.previewbin` binary cache format (Payload ID `0x0009`).
- **JMH Microbenchmark Suite**: Profiling >162M metadata decodes/sec and >36.5M encodes/sec.
