# FastPreview 0.1.1 [ALPHA] — Native Document, PDF & Code Rendering Engine

[![Status](https://img.shields.io/badge/status-0.1.1-brightgreen.svg)](https://github.com/andrestubbe/FastPreview/releases/tag/0.1.1)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Cross--Platform-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastPreview)

---

**⚡ High-performance multi-backend document rendering (PDFium / PDFBox / WebView2), source code syntax rendering, zero-copy pixel buffers, and `.previewbin` binary cache for Java.**

**FastPreview** replaces slow and heavy Swing/JavaFX preview components. It renders PDFs, Markdown, HTML, source code, and screenshots directly into GPU-friendly `FastImage` ARGB buffers with native speed and sub-millisecond `.previewbin` streaming.

---

## Quick Start

```java
import fastpreview.api.*;
import java.io.File;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
        // 1. Initialize FastPreview engine
        FastPreview engine = new FastPreview();

        // 2. Render document preview
        File doc = new File("C:\\Docs\\architecture.pdf");
        PreviewRequest request = new PreviewRequest(doc, 1920, 1080);
        PreviewResult result = engine.render(request);

        if (result.isSuccess()) {
            System.out.printf("Rendered preview in %d ms via %s\n",
                    result.getRenderTimeMs(), result.getBackendUsed());
        }

        // 3. Compact FastFileFormat Binary Metadata Cache (.previewbin)
        PreviewRecord record = new PreviewRecord(
                doc.getAbsolutePath(), result.getRenderTimeMs(), 1920, 1080,
                result.getBackendUsed() != null ? result.getBackendUsed().name() : "NATIVE",
                result.isSuccess()
        );

        byte[] binary = PreviewCodec.encode(List.of(record));
        List<PreviewRecord> restored = PreviewCodec.decode(binary);
    }
}
```

---

## Key Features

- **📄 Multi-Engine Document Rendering** — Hybrid PDF rendering supporting both native PDFium and Apache PDFBox 3.0.
- **💻 Syntax & Markup Previews** — Ultra-fast rendering of Markdown, HTML, and Java/C/Rust source code files.
- **⚡ Zero-Copy FastImage Output** — Direct pixel buffer rasterization ready for DirectX, Vulkan, and Swing integration.
- **📦 FastFileFormat `.previewbin` Cache** — Compact VarInt binary serialization for preview metadata caches (Payload ID `0x0009`).

---

## Real-World Scenarios

- **📑 Enterprise Document Portals** — Generating instantaneous high-resolution previews for large document repositories.
- **💻 IDEs & Code Browsers** — High-speed file previews and multi-language syntax previews without spinning up full editor instances.
- **📂 File Explorers** — Fast content preview panes for PDFs, SVGs, HTML, and markdown documents.

---

## Performance Benchmarks

FastPreview is profiled using **JMH** to guarantee zero bottleneck during high-concurrency document rendering.

| Benchmark Operation | Score (ops/ms) | Throughput | Memory Overhead |
|---|---|---|---|
| **Binary Preview Cache Decoding (`.previewbin`)** | **~162,000 ops/ms** | **> 162 Million records/sec** | **Zero-Copy Streaming** |
| **Binary Preview Cache Encoding (`.previewbin`)** | **~36,500 ops/ms** | **> 36.5 Million records/sec** | **Compact VarInt Delta Buffer** |

*Run the benchmarks locally:* `.\run-benchmark.bat`

---

## API Quick Reference

| Method / Class | Description |
|---|---|
| `new FastPreview()` | Initializes core document rendering engine. |
| `engine.render(request)` | Renders document to ARGB pixel buffer using default backend. |
| `engine.render(request, backend)` | Renders document with specified backend (PDFBOX, NATIVE). |
| `PreviewCodec.encode(records)` | Serializes preview metadata records into compressed FastFileFormat bytes. |
| `PreviewCodec.decode(bytes)` | Deserializes `.previewbin` bytes back into `List<PreviewRecord>`. |

---

## Technical Examples & Hero Demos

| Case | Java Example | Launcher | Description |
|---|---|---|---|
| **Live Document Preview Demo** | [Demo.java](examples/Demo/src/main/java/fastpreview/demo/Demo.java) | `run-demo.bat` | Multi-format rendering and `.previewbin` binary cache serialization. |
| **JMH Microbenchmark Suite** | [Benchmark.java](examples/Benchmark/src/main/java/fastpreview/benchmark/Benchmark.java) | `run-benchmark.bat` | High-throughput binary codec serialization and streaming benchmarks. |

---

## Installation

### Option 1: Maven (JitPack)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastPreview</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastImage</artifactId>
        <version>0.1.1</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastFileFormat</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastCore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastPreview:0.1.0'
    implementation 'com.github.andrestubbe:FastImage:0.1.1'
    implementation 'com.github.andrestubbe:FastFileFormat:0.1.0'
    implementation 'com.github.andrestubbe:FastCore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JARs directly to add them to your classpath:

1. 📄 **[FastPreview-0.1.0.jar](https://github.com/andrestubbe/FastPreview/releases/download/0.1.0/FastPreview-0.1.0.jar)** (Document Preview Engine)
2. ⚡ **[FastImage-0.1.1.jar](https://github.com/andrestubbe/FastImage/releases/download/0.1.1/FastImage-0.1.1.jar)** (Zero-Copy Image Manipulation)
3. 📄 **[FastFileFormat-0.1.0.jar](https://github.com/andrestubbe/FastFileFormat/releases/download/0.1.0/FastFileFormat-0.1.0.jar)** (Dual Binary & Text File Format)
4. ⚙️ **[FastCore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.0/FastCore-0.1.0.jar)** (Foundation Library)

---

## Documentation

* **[REFERENCE.md](docs/REFERENCE.md)**: Full API reference and method signatures.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Architectural design principles and rendering pipeline.
* **[CHANGELOG.md](docs/CHANGELOG.md)**: Release history and version notes.
* **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones and planned features.
* **[COMPILE.md](docs/COMPILE.md)**: Instructions for compiling from source.

---

## License

MIT License. See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastThumb](https://github.com/andrestubbe/FastThumb) — Native Windows Shell thumbnail & icon extractor
- [FastImage](https://github.com/andrestubbe/FastImage) — SIMD-accelerated image scaling and pixel buffers
- [FastFileFormat](https://github.com/andrestubbe/FastFileFormat) — Universal dual-format binary & text document engine

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*
