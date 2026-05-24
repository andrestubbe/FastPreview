# FastPreview v0.1.0 [ALPHA] — High-Performance Content Rendering for Java

[![Status](https://img.shields.io/badge/status-v0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastPreview/releases/tag/v0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe)

**⚡ Lightweight native rendering capabilities for PDF, HTML, Code, Text, Markdown, and SVG.**

FastPreview provides **high-speed content rasterization** for Java applications. It delivers off-heap pixel buffers
directly to `FastImage`, bypassing the overhead of traditional UI frameworks.

[![FastKeyboard Showcase](docs/screenshot.png)](https://www.youtube.com/watch?v=BZsqQl7WqWk)

```java
// Quick Start — Render a PDF page to FastImage

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

> [!IMPORTANT]
> All JARs must be in your classpath for the native JNI calls to function correctly.

## Try the Demo

1. Clone this repository
2. Run `run-demo.bat`
3. Or navigate to `examples/DemoPDF` and run `mvn exec:java`

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

## Platform Support

| Platform            | Status            |
|---------------------|-------------------|
| Windows 10/11 (x64) | ✅ Fully Supported |
| Linux               | 🚧 Planned        |
| macOS               | 🚧 Planned        |

---

## Building from Source

For detailed instructions on compiling the C++ JNI code and building the Maven FatJAR, see [COMPILE.md](COMPILE.md).

---

## License

MIT License — See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastCore](https://github.com/andrestubbe/FastCore) — Native Library Loader for Java
- [FastKeyboard](https://github.com/andrestubbe/FastKeyboard) — High-performance RawInput engine
- [FastTheme](https://github.com/andrestubbe/FastTheme) — Advanced UI styling engine

---
**Part of the FastJava Ecosystem** — *Making the JVM faster.*



<!-- 
SEO Keywords: java, jni, native, fastpreview, pdfium, webview2, skia, rendering, performance
-->
