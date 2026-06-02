# FastPreview v0.1.0 [ALPHA] — High-Performance Content Rendering for Java

[![Status](https://img.shields.io/badge/status-v0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastPreview/releases/tag/v0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe)

---

**⚡ Lightweight native rendering capabilities for PDF, HTML, Code, Text, Markdown, and SVG.**

FastPreview provides **high-speed content rasterization** for Java applications. It delivers off-heap pixel buffers
directly to `FastImage`, bypassing the overhead of traditional UI frameworks.

---

[![FastKeyboard Showcase](docs/screenshot.png)](https://www.youtube.com/watch?v=BZsqQl7WqWk)

---

## Table of Contents

- [Key Features](#key-features)
- [Architecture](#architecture)
- [Installation](#installation)
- [Try the Demo](#try-the-demo)
- [Backends](#backends)
- [Platform Support](#platform-support)
- [Building from Source](#building-from-source)
- [License](#license)
- [Related Projects](#related-projects)

---

## Quick Start

```java
import fastpreview.api.FastPreview;
import fastpreview.api.PreviewRequest;
import fastpreview.api.PreviewResult;

public class Demo {
    public static void main(String[] args) {
        FastPreview api = new FastPreview();
        PreviewRequest request = new PreviewRequest(new File("document.pdf"), 800, 600);

        PreviewResult result = api.render(request);

        if (result.isSuccess()) {
            System.out.println("Rendered in " + (result.getRenderTimeNanos() / 1_000_000.0) + " ms");
        }
    }
}
```

---

## Key Features

- **🚀 Native Performance** — Direct integration with PDFium, WebView2, and Skia.
- **⚡ Zero-Copy** — Off-heap pixel buffers delivered directly to FastImage.
- **📦 Modular Backends** — Pluggable support for PDF, HTML, Markdown, and more.
- **🎯 Deterministic** — Predictable rendering times for batch processing.

---

## Architecture

FastPreview follows a strict pipeline:
`Content → Layout → Rasterization → PixelBuffer → FastImage`

For more details, see [ARCHITECTURE.md](docs/ARCHITECTURE.md).

---

## Installation

### Option 1: Maven (Recommended)

Add the JitPack repository and the dependencies to your `pom.xml`:

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
<!-- FastPreview Library -->
<dependency>
    <groupId>com.github.andrestubbe</groupId>
    <artifactId>fastpreview</artifactId>
    <version>v0.1.0</version>
</dependency>

<!-- FastCore (Required Native Loader) -->
<dependency>
    <groupId>com.github.andrestubbe</groupId>
    <artifactId>fastcore</artifactId>
    <version>v0.1.0</version>
</dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:fastpreview:v0.1.0'
    implementation 'com.github.andrestubbe:fastcore:v0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JARs directly to add them to your classpath:

1. 📦 *
   *[fastpreview-v0.1.0.jar](https://github.com/andrestubbe/FastPreview/releases/download/v0.1.0/fastpreview-v0.1.0.jar)
   ** (The Core Library)
2. ⚙️ **[fastcore-v0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/v0.1.0/fastcore-v0.1.0.jar)** (
   The Mandatory Native Loader)

---

## Backends

| Content Type | Backend             | Status         |
|--------------|---------------------|----------------|
| PDF          | PDFium              | 🚧 Integrating |
| HTML         | WebView2            | 🚧 Integrating |
| Code         | Syntax + FastTheme  | ✅ Skeleton     |
| Text         | DirectWrite         | 🚧 Planned     |
| Markdown     | Custom AST Renderer | 🚧 Planned     |
| SVG          | Skia                | 🚧 Planned     |

---

## Documentation

* **[COMPILE.md](docs/COMPILE.md)**: Full compilation guide (MSVC C++17 build chain + JNI Setup).
* **[REFERENCE.md](docs/REFERENCE.md)**: Full API descriptions, border configurations, and codepoint index.
* **[PHILOSOPHIE.md](PHILOSOPHIE.md)**: The engineering rationale for zero-allocation performance.
* **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones and planned features.

---

## Platform Support

| Platform      | Status            |
|---------------|-------------------|
| Windows 10/11 | ✅ Fully Supported |
| Linux         | 🚧 Planned        |
| macOS         | 🚧 Planned        |

---

## License

MIT License — See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastFileIndex](https://github.com/andrestubbe/FastFileIndex) - Binary file indexing with mmap support
- [FastFileSearch](https://github.com/andrestubbe/FastFileSearch) - Prefix Trie, N-Gram index, and Ranking engine
- [FastFileWatch](https://github.com/andrestubbe/FastFileWatch) - USN Journal-based live file monitoring
- [FastCore](https://github.com/andrestubbe/FastCore) - Unified JNI loader and platform abstraction

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*




