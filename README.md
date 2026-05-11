# FastPreview — High-Performance Content Rendering for Java

**Lightweight native rendering capabilities for PDF, HTML, Code, Text, Markdown, and SVG.**

[![Build](https://img.shields.io/github/actions/workflow/status/andrestubbe/FastPreview/maven.yml?branch=main)](https://github.com/andrestubbe/FastPreview/actions)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![JitPack](https://jitpack.io/v/andrestubbe/FastPreview.svg)](https://jitpack.io/#andrestubbe/FastPreview)

FastPreview provides **high-speed content rasterization** for Java applications. It delivers off-heap pixel buffers directly to `FastImage`, bypassing the overhead of traditional UI frameworks.

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

FastJava modules require **two** dependencies: the module itself, and `FastCore`.

### Maven (JitPack)
```xml
<dependencies>
    <!-- 1. FastPreview Module -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastpreview</artifactId>
        <version>0.1.0</version>
    </dependency>
    
    <!-- 2. FastCore (Required for native loading) -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

---

## Try the Demo

1. Clone this repository
2. Run `run-demo.bat`
3. Or navigate to `examples/DemoPDF` and run `mvn exec:java`

---

## Backends

| Content Type | Backend | Status |
|--------------|---------|--------|
| PDF | PDFium | 🚧 Integrating |
| HTML | WebView2 | 🚧 Integrating |
| Code | Syntax + FastTheme | ✅ Skeleton |
| Text | DirectWrite | 🚧 Planned |
| Markdown | Custom AST Renderer | 🚧 Planned |
| SVG | Skia | 🚧 Planned |

---

## Platform Support

| Platform | Status |
|----------|--------|
| Windows 10/11 (x64) | ✅ Fully Supported |
| Linux | 🚧 Planned |
| macOS | 🚧 Planned |

---

## Building from Source

For detailed instructions on compiling the C++ JNI code and building the Maven FatJAR, see [COMPILE.md](COMPILE.md).

---

## License
MIT License — See [LICENSE](LICENSE) file for details.

---

## Related Projects
- [FastImage](https://github.com/andrestubbe/FastImage) — High-performance image processing
- [FastCore](https://github.com/andrestubbe/FastCore) — Native Library Loader for Java
- [FastTheme](https://github.com/andrestubbe/FastTheme) — Unified styling and colors

---
**Made with ⚡ by Andre Stubbe**

<!-- 
SEO Keywords: java, jni, native, fastpreview, pdfium, webview2, skia, rendering, performance
-->
