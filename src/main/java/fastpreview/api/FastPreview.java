package fastpreview.api;

import fastcore.FastCore;
import fastpreview.pixel.PixelBuffer;
import fastpreview.pixel.PixelFormat;
import java.io.File;

/**
 * Main entry point for FastPreview rendering.
 */
public class FastPreview {

    static {
        // Load pdfium dependency first, so it is loaded in the process space
        fastcore.FastCore.loadLibrary("pdfium");
        // Load our native JNI DLL
        fastcore.FastCore.loadLibrary("fastpreview");
    }

    /**
     * Renders a preview based on the request using default PDFBOX backend.
     */
    public PreviewResult render(PreviewRequest request) {
        return render(request, PreviewBackend.PDFBOX);
    }

    /**
     * Renders a preview based on the request using the specified backend.
     */
    public PreviewResult render(PreviewRequest request, PreviewBackend backend) {
        String fileName = request.getSource().getName().toLowerCase();
        long start = System.nanoTime();
        
        try {
            if (fileName.endsWith(".pdf")) {
                if (backend == PreviewBackend.NATIVE) {
                    return renderPDFNativeBackend(request, start);
                } else {
                    return renderPDFBox(request, start);
                }
            } else if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
                return renderHTML(request, start);
            } else if (fileName.endsWith(".java") || fileName.endsWith(".txt")) {
                return renderText(request, start);
            } else if (fileName.endsWith(".md")) {
                return renderMarkdown(request, start);
            } else if (fileName.endsWith(".svg")) {
                return renderSVG(request, start);
            } else if (fileName.contains("screen")) {
                return renderScreenshot(request, start);
            } else {
                return new PreviewResult("Unsupported file format: " + fileName);
            }
        } catch (Exception e) {
            return new PreviewResult("Rendering failed: " + e.getMessage());
        }
    }

    private PreviewResult renderPDFBox(PreviewRequest request, long startTime) {
        try {
            // Load the document using PDFBox
            org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.Loader.loadPDF(request.getSource());
            org.apache.pdfbox.rendering.PDFRenderer renderer = new org.apache.pdfbox.rendering.PDFRenderer(document);
            
            // Calculate scale to render at exactly the requested width
            org.apache.pdfbox.pdmodel.PDPage page = document.getPage(request.getPageIndex());
            float pageWidth = page.getMediaBox().getWidth();
            float scale = (float) request.getWidth() / pageWidth;
            
            java.awt.image.BufferedImage img = renderer.renderImage(request.getPageIndex(), scale);
            document.close();
            
            int w = img.getWidth();
            int h = img.getHeight();
            
            // Allocate our off-heap PixelBuffer
            PixelBuffer buffer = new PixelBuffer(w, h, PixelFormat.BGRA32);
            java.nio.ByteBuffer byteBuf = buffer.getBuffer();
            
            // Get pixels and convert from ARGB (Java) to BGRA (native) format
            int[] pixels = new int[w * h];
            img.getRGB(0, 0, w, h, pixels, 0, w);
            byteBuf.rewind();
            for (int p : pixels) {
                int a = (p >> 24) & 0xFF;
                int r = (p >> 16) & 0xFF;
                int g = (p >> 8) & 0xFF;
                int b = p & 0xFF;
                // BGRA format: (B << 24) | (G << 16) | (R << 8) | A
                int bgra = (b << 24) | (g << 16) | (r << 8) | a;
                byteBuf.putInt(bgra);
            }
            
            return new PreviewResult(buffer, System.nanoTime() - startTime);
        } catch (Exception e) {
            return new PreviewResult("PDFBox backend failed: " + e.getMessage());
        }
    }

    private PreviewResult renderPDFNativeBackend(PreviewRequest request, long startTime) {
        try {
            int w = request.getWidth();
            int h = request.getHeight();
            
            // Allocate off-heap PixelBuffer matching the exact requested dimensions
            PixelBuffer buffer = new PixelBuffer(w, h, PixelFormat.BGRA32);
            java.nio.ByteBuffer byteBuf = buffer.getBuffer();
            
            // Invoke the JNI C++ function to render directly at this exact resolution
            boolean success = renderPDFNative(
                request.getSource().getAbsolutePath(),
                request.getPageIndex(),
                w,
                h,
                byteBuf
            );
            
            if (success) {
                return new PreviewResult(buffer, System.nanoTime() - startTime);
            } else {
                return new PreviewResult("Native PDF rendering returned failure.");
            }
        } catch (Exception e) {
            return new PreviewResult("Native rendering exception: " + e.getMessage());
        }
    }

    private PreviewResult renderHTML(PreviewRequest request, long startTime) {
        PixelBuffer buffer = new PixelBuffer(request.getWidth(), request.getHeight(), PixelFormat.BGRA32);
        buffer.clear(0xFF00FF00); 
        return new PreviewResult(buffer, System.nanoTime() - startTime);
    }

    private PreviewResult renderText(PreviewRequest request, long startTime) {
        if (request.getSource().getName().endsWith(".java")) {
            return renderCode(request, startTime);
        }
        PixelBuffer buffer = new PixelBuffer(request.getWidth(), request.getHeight(), PixelFormat.BGRA32);
        buffer.clear(0xFF222222); 
        return new PreviewResult(buffer, System.nanoTime() - startTime);
    }

    private PreviewResult renderCode(PreviewRequest request, long startTime) {
        PixelBuffer buffer = new PixelBuffer(request.getWidth(), request.getHeight(), PixelFormat.BGRA32);
        buffer.clear(0xFF1E1E1E); 
        return new PreviewResult(buffer, System.nanoTime() - startTime);
    }

    private PreviewResult renderMarkdown(PreviewRequest request, long startTime) {
        PixelBuffer buffer = new PixelBuffer(request.getWidth(), request.getHeight(), PixelFormat.BGRA32);
        buffer.clear(0xFFF0F0F0); 
        return new PreviewResult(buffer, System.nanoTime() - startTime);
    }

    private PreviewResult renderSVG(PreviewRequest request, long startTime) {
        PixelBuffer buffer = new PixelBuffer(request.getWidth(), request.getHeight(), PixelFormat.BGRA32);
        buffer.clear(0xFF00FFFF); 
        return new PreviewResult(buffer, System.nanoTime() - startTime);
    }

    private PreviewResult renderScreenshot(PreviewRequest request, long startTime) {
        PixelBuffer buffer = new PixelBuffer(request.getWidth(), request.getHeight(), PixelFormat.BGRA32);
        buffer.clear(0xFFFFFF00); 
        return new PreviewResult(buffer, System.nanoTime() - startTime);
    }

    public long getPageSize(File file, int pageIndex, int dpi) {
        try {
            return getPageSizeNative(file.getAbsolutePath(), pageIndex, dpi);
        } catch (UnsatisfiedLinkError e) {
            // Fallback for PDFBox or mock if JNI isn't fully linked
            return ((long)800 << 32) | 1100;
        }
    }

    public PreviewResult renderEx(
        PreviewRequest request, 
        int viewWidth, 
        int viewHeight, 
        double offsetX, 
        double offsetY, 
        int pageWidth, 
        int pageHeight, 
        PreviewBackend backend
    ) {
        long start = System.nanoTime();
        try {
            if (backend == PreviewBackend.NATIVE) {
                // Viewport size PixelBuffer
                PixelBuffer buffer = new PixelBuffer(viewWidth, viewHeight, PixelFormat.BGRA32);
                java.nio.ByteBuffer byteBuf = buffer.getBuffer();
                
                boolean success = renderPDFNativeEx(
                    request.getSource().getAbsolutePath(),
                    request.getPageIndex(),
                    viewWidth,
                    viewHeight,
                    offsetX,
                    offsetY,
                    pageWidth,
                    pageHeight,
                    byteBuf
                );
                
                if (success) {
                    return new PreviewResult(buffer, System.nanoTime() - start);
                } else {
                    return new PreviewResult("Native PDF renderEx returned failure.");
                }
            } else {
                // For PDFBox fallback: render full scaled page (no viewport clipping) to enable borderless, 60 FPS panning
                PreviewRequest fullReq = new PreviewRequest(request.getSource(), pageWidth, pageHeight, request.getDpi(), request.getPageIndex());
                return renderPDFBox(fullReq, start);
            }
        } catch (Exception e) {
            return new PreviewResult("renderEx failed: " + e.getMessage());
        }
    }

    // === JNI Declarations ===
    private static native long getPageSizeNative(
        String filePath, 
        int pageIndex, 
        int dpi
    );

    private static native boolean renderPDFNative(
        String filePath, 
        int pageIndex, 
        int width, 
        int height, 
        java.nio.ByteBuffer buffer
    );

    private static native boolean renderPDFNativeEx(
        String filePath, 
        int pageIndex, 
        int viewWidth, 
        int viewHeight, 
        double offsetX, 
        double offsetY, 
        int pageWidth, 
        int pageHeight, 
        java.nio.ByteBuffer buffer
    );
}
