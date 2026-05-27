package fastpreview.demo;

import fastpreview.api.FastPreview;
import fastpreview.api.PreviewBackend;
import fastpreview.api.PreviewRequest;
import fastpreview.api.PreviewResult;
import fastpreview.pixel.PixelBuffer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;

public class DemoPDF {

    /**
     * Dynamic overscan padding based on zoom level.
     * Higher zoom = more padding to prevent edge artifacts during fast panning.
     * Lower zoom = less padding to avoid wasting render budget on invisible pixels.
     */
    private static int getDynamicPadding(double zoom) {
        if (zoom >= 2.0) return 200;
        if (zoom >= 1.0) return 120;
        if (zoom >= 0.5) return 80;
        return 40;
    }
    private static int currentPage = 0;
    private static int totalPages = 1;
    private static PreviewBackend currentBackend = PreviewBackend.PDFBOX;
    private static FastPreview previewEngine;
    private static File pdfFile;
    private static PDFCanvas canvas;
    private static JFrame frame;
    
    // Base resolution at 150 DPI (aspect ratio standard)
    private static int baseWidth = 1275;
    private static int baseHeight = 1650;
    
    // Debounce timer for high-res snapping
    private static Timer renderDebounceTimer;
    
    // FPS and Throttling metrics
    private static long lastRenderNanos = 0;
    private static long lastFpsCalcTime = 0;
    private static int frameCount = 0;
    private static double currentFps = 0.0;

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");

        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("FastPreview - PDF Interactive Dynamic Viewport Clipping Demo");

                pdfFile = new File("../assets/example.pdf");
                if (!pdfFile.exists()) {
                    JOptionPane.showMessageDialog(null, "PDF File not found at: " + pdfFile.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Initialize FastPreview engine
                previewEngine = new FastPreview();

                // 1. Get total page count and base dimensions at 150 DPI
                org.apache.pdfbox.pdmodel.PDDocument doc = org.apache.pdfbox.Loader.loadPDF(pdfFile);
                totalPages = doc.getNumberOfPages();
                doc.close();
                System.out.println("Loaded multipage PDF. Total pages: " + totalPages);

                long pageSize = previewEngine.getPageSize(pdfFile, currentPage, 150);
                if (pageSize != 0) {
                    baseWidth = (int) (pageSize >> 32);
                    baseHeight = (int) (pageSize & 0xFFFFFFFFL);
                }
                System.out.println(String.format("Base Page Size at 150 DPI: %dx%d", baseWidth, baseHeight));

                // 2. Create interactive canvas and frame
                canvas = new PDFCanvas();
                
                // Calculate initial window dimensions matching the PDF aspect ratio perfectly
                double aspectRatio = (double) baseWidth / baseHeight;
                int initialHeight = 820;
                int initialWidth = (int) (initialHeight * aspectRatio);
                
                // Adjust initial zoom and offset so the page perfectly aligns and fits the window
                canvas.offsetX = 0.0;
                canvas.offsetY = 0.0;
                canvas.visualZoom = (double) initialWidth / baseWidth;
                canvas.renderedZoom = canvas.visualZoom;
                
                frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(initialWidth + 16, initialHeight + 40);
                frame.setLocationRelativeTo(null);
                frame.add(canvas);

                // 3. Setup the 60ms debounce timer for high-res render snapping
                renderDebounceTimer = new Timer(60, e -> {
                    renderCurrentPage(canvas.getVisualZoom());
                });
                renderDebounceTimer.setRepeats(false);

                // 4. Add key listener for backend toggling and page navigation
                frame.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_B) {
                            // Toggle backend
                            currentBackend = (currentBackend == PreviewBackend.PDFBOX) ? PreviewBackend.NATIVE : PreviewBackend.PDFBOX;
                            System.out.println("Switched backend to: " + currentBackend);
                            renderCurrentPage(canvas.getVisualZoom());
                        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                            // Next page
                            if (currentPage < totalPages - 1) {
                                currentPage++;
                                updatePageSize();
                                renderCurrentPage(canvas.getVisualZoom());
                            }
                        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                            // Previous page
                            if (currentPage > 0) {
                                currentPage--;
                                updatePageSize();
                                renderCurrentPage(canvas.getVisualZoom());
                            }
                        }
                    }
                });

                // Request focus for keyboard input
                frame.setFocusable(true);
                frame.requestFocusInWindow();

                // 5. Initial render
                renderCurrentPage(canvas.getVisualZoom());

                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to start demo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void updatePageSize() {
        long pageSize = previewEngine.getPageSize(pdfFile, currentPage, 150);
        if (pageSize != 0) {
            baseWidth = (int) (pageSize >> 32);
            baseHeight = (int) (pageSize & 0xFFFFFFFFL);
        }
    }

    private static void renderCurrentPage(double zoomToRender) {
        if (previewEngine == null || pdfFile == null || canvas == null) return;

        // Target viewport dimensions
        int viewW = canvas.getWidth();
        int viewH = canvas.getHeight();
        if (viewW <= 0) viewW = 1100;
        if (viewH <= 0) viewH = 850;
        
        // Dynamic overscan padding - scales with zoom to balance quality vs. speed
        int padding = getDynamicPadding(zoomToRender);
        
        // Padded viewport size for JNI overscan rendering
        int paddedW = viewW + 2 * padding;
        int paddedH = viewH + 2 * padding;
 
        // Virtual scaled page dimensions
        int pageWidth = (int) (baseWidth * zoomToRender);
        int pageHeight = (int) (baseHeight * zoomToRender);
        
        pageWidth = Math.max(pageWidth, 1);
        pageHeight = Math.max(pageHeight, 1);
 
        // Ensure offsets are dynamically clamped and centered
        canvas.clampAndCenterOffsets();
 
        // Get current panning offsets from canvas
        double offsetX = canvas.getOffsetX();
        double offsetY = canvas.getOffsetY();
        
        // Shift render coordinates for JNI inside the padded buffer
        double shiftedOffsetX = offsetX + padding;
        double shiftedOffsetY = offsetY + padding;
 
        // Create a request carrying virtual page dimensions
        PreviewRequest request = new PreviewRequest(pdfFile, pageWidth, pageHeight, 150, currentPage);
        
        // Execute dynamic viewport-clipped renderEx (PDFBox renders full-page, Native renders padded)
        PreviewResult result = previewEngine.renderEx(
            request, 
            paddedW, 
            paddedH, 
            shiftedOffsetX, 
            shiftedOffsetY, 
            pageWidth, 
            pageHeight, 
            currentBackend
        );
 
        if (result.isSuccess()) {
            PixelBuffer buffer = result.getPixelBuffer();
            BufferedImage img = pixelBufferToImage(buffer);
            canvas.setImage(img, zoomToRender, offsetX, offsetY, currentBackend, padding);
            
            double renderTimeMs = result.getRenderTimeNanos() / 1_000_000.0;
            
            // Calculate dynamic FPS (sliding average over 500ms)
            long nowNanos = System.nanoTime();
            frameCount++;
            if (lastFpsCalcTime == 0) {
                lastFpsCalcTime = nowNanos;
            } else if (nowNanos - lastFpsCalcTime >= 500_000_000L) {
                double elapsedSec = (nowNanos - lastFpsCalcTime) / 1_000_000_000.0;
                currentFps = frameCount / elapsedSec;
                frameCount = 0;
                lastFpsCalcTime = nowNanos;
            }
            
            // Update window title with benchmark and FPS details
            String title = String.format("FastPreview [%s] | Page: %d/%d | Viewport: %dx%d (Page: %dx%d) | Time: %.2f ms | FPS: %.1f | Controls: [B] Backend, [Arrows] Page",
                    currentBackend.name(), (currentPage + 1), totalPages, viewW, viewH, pageWidth, pageHeight, renderTimeMs, currentFps);
            frame.setTitle(title);

            // Log to console (Debug outputs)
            String logLine = String.format("[BENCHMARK] Backend: %-8s | Page: %2d/%2d | Viewport: %4dx%-4d | Render Time: %7.2f ms",
                    currentBackend.name(), (currentPage + 1), totalPages, viewW, viewH, renderTimeMs);
            System.out.println(logLine);

            // Write to a local CSV file for persistent tracking
            try {
                File csvFile = new File("benchmark_results.csv");
                boolean exists = csvFile.exists();
                java.io.FileWriter writer = new java.io.FileWriter(csvFile, true);
                if (!exists) {
                    writer.write("Timestamp,Backend,Page,Width,Height,RenderTimeMs\n");
                }
                String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                writer.write(String.format("%s,%s,%d,%d,%d,%.4f\n", 
                        timestamp, currentBackend.name(), (currentPage + 1), viewW, viewH, renderTimeMs));
                writer.close();
            } catch (Exception ex) {
                System.err.println("[WARN] Failed to write benchmark to CSV: " + ex.getMessage());
            }
        } else {
            System.err.println("Render error: " + result.getErrorMessage());
            frame.setTitle("FastPreview - Render Error: " + result.getErrorMessage());
        }
    }

    /**
     * Converts an off-heap PixelBuffer (BGRA32) into a standard Java BufferedImage (ARGB).
     */
    private static BufferedImage pixelBufferToImage(PixelBuffer pixelBuf) {
        int w = pixelBuf.getWidth();
        int h = pixelBuf.getHeight();
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int[] targetPixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        
        java.nio.ByteBuffer byteBuf = pixelBuf.getBuffer();
        byteBuf.rewind();
        
        // Bulk transfer using IntBuffer: extremely fast (< 0.2ms)
        java.nio.IntBuffer intBuf = byteBuf.asIntBuffer();
        intBuf.get(targetPixels);
        
        return img;
    }

    private static class PDFCanvas extends JPanel {
        private BufferedImage image;
        private double visualZoom = 0.5;  // Instant visual scale factor
        private double renderedZoom = 0.5; // Scale factor at which the current image was rendered
        private double offsetX = 150.0;
        private double offsetY = 30.0;
        private double renderedOffsetX = 150.0; // Panning offset when the current image was rendered
        private double renderedOffsetY = 30.0;
        private PreviewBackend renderedBackend = PreviewBackend.PDFBOX; // Backend that generated the current image
        private int renderedPadding = 120; // Overscan padding used when the current image was rendered
        private Point lastDragPoint;

        public PDFCanvas() {
            setBackground(new Color(20, 20, 20)); // Deep dark theme

            // High-Precision Zoom relative to Mouse Pointer (fixes Touchpads/Precision Mice)
            addMouseWheelListener(e -> {
                double rotation = e.getPreciseWheelRotation();
                if (rotation == 0.0) return;

                double oldZoom = visualZoom;
                double zoomFactor = Math.pow(1.15, -rotation);
                
                visualZoom *= zoomFactor;
                
                // Limit zoom levels (5% to 800%)
                visualZoom = Math.max(0.05, Math.min(visualZoom, 8.0));
                
                // Adjust offsets so the point under the mouse stays in place
                double mouseX = e.getX();
                double mouseY = e.getY();
                offsetX = mouseX - (mouseX - offsetX) * (visualZoom / oldZoom);
                offsetY = mouseY - (mouseY - offsetY) * (visualZoom / oldZoom);
                
                clampAndCenterOffsets();
                repaint();
 
                // Trigger or restart the snapping timer for debounced render
                if (renderDebounceTimer != null) {
                    renderDebounceTimer.restart();
                }
            });

            // Mouse Drag to Pan
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    lastDragPoint = e.getPoint();
                    // Regain keyboard focus when clicking
                    frame.requestFocusInWindow();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (lastDragPoint != null) {
                        int dx = e.getX() - lastDragPoint.x;
                        int dy = e.getY() - lastDragPoint.y;
                        offsetX += dx;
                        offsetY += dy;
                        lastDragPoint = e.getPoint();
                        
                        clampAndCenterOffsets();
                        repaint(); // Butter-smooth GPU-accelerated interim panning in visual space
                        
                        if (renderDebounceTimer != null) {
                            renderDebounceTimer.restart();
                        }
                    }
                }
            });
        }

        public void clampAndCenterOffsets() {
            int viewW = getWidth();
            int viewH = getHeight();
            if (viewW <= 0) viewW = 1100;
            if (viewH <= 0) viewH = 850;
 
            int pageWidth = (int) (baseWidth * visualZoom);
            int pageHeight = (int) (baseHeight * visualZoom);
 
            // Centering horizontally if page fits in screen
            if (pageWidth < viewW) {
                offsetX = (viewW - pageWidth) / 2.0;
            } else {
                // Constrain panning so page doesn't go fully off-screen
                offsetX = Math.max(-pageWidth + 100, Math.min(viewW - 100, offsetX));
            }
 
            // Centering vertically if page fits in screen
            if (pageHeight < viewH) {
                offsetY = (viewH - pageHeight) / 2.0;
            } else {
                // Constrain panning so page doesn't go fully off-screen
                offsetY = Math.max(-pageHeight + 100, Math.min(viewH - 100, offsetY));
            }
        }

        public double getVisualZoom() {
            return visualZoom;
        }

        public double getOffsetX() {
            return offsetX;
        }

        public double getOffsetY() {
            return offsetY;
        }

        public void setImage(BufferedImage img, double zoomLevel, double renderedX, double renderedY, PreviewBackend backend, int padding) {
            this.image = img;
            this.renderedZoom = zoomLevel;
            this.renderedOffsetX = renderedX;
            this.renderedOffsetY = renderedY;
            this.renderedBackend = backend;
            this.renderedPadding = padding;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image == null) return;

            Graphics2D g2d = (Graphics2D) g;

            // Use bilinear interpolation for smooth scaling during panning/interim zooming
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // Compute relative scale and offset shift
            double relativeScale = visualZoom / renderedZoom;
            
            AffineTransform at = new AffineTransform();
            
            // Backend-aware drawing strategy
            if (renderedBackend == PreviewBackend.NATIVE) {
                // Viewport-clipped Native JNI image (drawn translated by overscan padding)
                double interimX = offsetX - renderedOffsetX * relativeScale;
                double interimY = offsetY - renderedOffsetY * relativeScale;
                at.translate(interimX - renderedPadding * relativeScale, interimY - renderedPadding * relativeScale);
            } else {
                // Full scaled page image (PDFBox backend) - draw at current offset directly
                at.translate(offsetX, offsetY);
            }
            at.scale(relativeScale, relativeScale);
 
            // Draw PDF page
            g2d.drawImage(image, at, null);
        }
    }
}
