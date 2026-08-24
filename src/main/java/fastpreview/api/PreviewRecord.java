package fastpreview.api;

/**
 * Metadata record describing a rendered document preview.
 *
 * @param sourcePath Path of source document.
 * @param renderTimeMs Milliseconds taken to render.
 * @param width Width in pixels.
 * @param height Height in pixels.
 * @param backend Used backend engine (PDFBOX, NATIVE, DIRECT).
 * @param success True if rendered without errors.
 */
public record PreviewRecord(
        String sourcePath,
        long renderTimeMs,
        int width,
        int height,
        String backend,
        boolean success
) {}
