package fastpreview.api;

import fastpreview.pixel.PixelBuffer;
import fastpreview.pixel.PixelFormat;

/**
 * Main entry point for FastPreview rendering.
 */
public class FastPreview {

    /**
     * Renders a preview based on the request.
     * Automatically selects the appropriate backend based on file extension.
     */
    public PreviewResult render(PreviewRequest request) {
        String fileName = request.getSource().getName().toLowerCase();
        
        long start = System.nanoTime();
        
        try {
            if (fileName.endsWith(".pdf")) {
                return renderPDF(request, start);
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

    private PreviewResult renderPDF(PreviewRequest request, long startTime) {
        // Mock implementation for initial API proof
        PixelBuffer buffer = new PixelBuffer(request.getWidth(), request.getHeight(), PixelFormat.BGRA32);
        
        // Simulating PDF rendering (Reddish background for PDF)
        buffer.clear(0xFFFF0000); 
        
        return new PreviewResult(buffer, System.nanoTime() - startTime);
    }

    private PreviewResult renderHTML(PreviewRequest request, long startTime) {
        // Mock implementation for initial API proof
        PixelBuffer buffer = new PixelBuffer(request.getWidth(), request.getHeight(), PixelFormat.BGRA32);
        
        // Simulating HTML rendering (Greenish background for HTML)
        buffer.clear(0xFF00FF00); 
        
        return new PreviewResult(buffer, System.nanoTime() - startTime);
    }

    private PreviewResult renderText(PreviewRequest request, long startTime) {
        // Mock implementation for initial API proof
        PixelBuffer buffer = new PixelBuffer(request.getWidth(), request.getHeight(), PixelFormat.BGRA32);
        
        // Simulating Text/Code rendering (Dark gray background)
        buffer.clear(0xFF222222); 
        
        return new PreviewResult(buffer, System.nanoTime() - startTime);
    }

    private PreviewResult renderMarkdown(PreviewRequest request, long startTime) {
        PixelBuffer buffer = new PixelBuffer(request.getWidth(), request.getHeight(), PixelFormat.BGRA32);
        
        // Simulating Markdown rendering (Light gray/white background)
        buffer.clear(0xFFF0F0F0); 
        
        return new PreviewResult(buffer, System.nanoTime() - startTime);
    }

    private PreviewResult renderSVG(PreviewRequest request, long startTime) {
        PixelBuffer buffer = new PixelBuffer(request.getWidth(), request.getHeight(), PixelFormat.BGRA32);
        
        // Simulating SVG rendering (Yellowish background)
        buffer.clear(0xFF00FFFF); 
        
        return new PreviewResult(buffer, System.nanoTime() - startTime);
    }

    private PreviewResult renderScreenshot(PreviewRequest request, long startTime) {
        PixelBuffer buffer = new PixelBuffer(request.getWidth(), request.getHeight(), PixelFormat.BGRA32);
        
        // Simulating Screenshot capture (Cyan background)
        buffer.clear(0xFFFFFF00); 
        
        return new PreviewResult(buffer, System.nanoTime() - startTime);
    }
}
