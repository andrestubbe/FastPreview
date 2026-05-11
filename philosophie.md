# The Philosophy of FastPreview

FastPreview is built on the principle that **content is pixels**. In a high-performance ecosystem, the bridge between a file on disk (PDF, HTML, Code) and its visual representation should be as short and direct as possible.

## Core Tenets

1. **Rendering is Not UI**
   FastPreview does not manage windows, buttons, or scrollbars. It is a pure rasterization layer that takes content as input and produces raw off-heap pixels as output.

2. **Native-First Precision**
   By leveraging industry-standard native engines like PDFium and WebView2, FastPreview ensures that previews are not just fast, but pixel-perfect and accurate to the original format.

3. **Zero-Copy Pipeline**
   Data movement is the enemy of performance. FastPreview allocates memory off-heap and delivers it directly to `FastImage`, ensuring that the rendering pipeline is as lean as possible.

4. **Deterministic and Batch-Ready**
   Whether rendering a single preview for an explorer window or batch-processing thousands of pages for a search index, FastPreview provides predictable performance and resource usage.

5. **Ecosystem Consistency**
   As part of the **FastJava** family, FastPreview adheres to the shared architecture:
   - **Unified Extraction**: Powered by `FastCore`.
   - **Performance Verifiability**: Integrated with standardized benchmarks.
   - **Standardized Styling**: Compatible with `FastTheme`.

## Why it matters
In modern autonomous systems and advanced agentic coding, the ability to "see" content quickly—without the overhead of a full browser or document viewer—is critical for decision-making and data processing. FastPreview is the "eyes" of the FastJava ecosystem.
