# FastPreview Reference Guide

## API Overview

`fastpreview.api.FastPreview` provides unified high-performance multi-engine rendering for PDFs, Markdown, HTML, and source code.

### Core Methods

| Method | Return | Description |
| :--- | :--- | :--- |
| `FastPreview.renderWithThumbnailFallback(File file, int w, int h)` | `PreviewResult` | Two-stage OS UI pipeline: FastThumb instant Shell preview first, falls back to full FastPreview render. |
| `FastPreview.render(PreviewRequest request)` | `PreviewResult` | Renders a preview using the default PDFBOX backend. |
| `FastPreview.render(PreviewRequest request, PreviewBackend backend)` | `PreviewResult` | Renders a preview with an explicit backend (PDFBOX or NATIVE). |

---

## Codec & Binary Serialization

`fastpreview.api.PreviewCodec` provides serialization of rendered preview metadata records into FastFileFormat `.previewbin` binaries (Payload ID `0x0009`).

| Method | Return | Description |
| :--- | :--- | :--- |
| `PreviewCodec.encode(List<PreviewRecord> records)` | `byte[]` | Encodes records to compressed `.previewbin` byte array. |
| `PreviewCodec.decode(byte[] bytes)` | `List<PreviewRecord>` | Deserializes `.previewbin` payload back to `PreviewRecord` list. |
| `PreviewCodec.writeToFile(Path path, List<PreviewRecord> records)` | `void` | Writes binary metadata cache directly to disk. |
| `PreviewCodec.readFromFile(Path path)` | `List<PreviewRecord>` | Reads binary metadata cache from disk. |