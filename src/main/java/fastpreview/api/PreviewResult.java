package fastpreview.api;

import fastpreview.pixel.PixelBuffer;

/**
 * Result of a rendering request.
 */
public class PreviewResult {
    private final PixelBuffer pixelBuffer;
    private final long renderTimeNanos;
    private final boolean success;
    private final String errorMessage;

    public PreviewResult(PixelBuffer pixelBuffer, long renderTimeNanos) {
        this.pixelBuffer = pixelBuffer;
        this.renderTimeNanos = renderTimeNanos;
        this.success = true;
        this.errorMessage = null;
    }

    public PreviewResult(String errorMessage) {
        this.pixelBuffer = null;
        this.renderTimeNanos = 0;
        this.success = false;
        this.errorMessage = errorMessage;
    }

    public PixelBuffer getPixelBuffer() {
        return pixelBuffer;
    }

    public long getRenderTimeNanos() {
        return renderTimeNanos;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
