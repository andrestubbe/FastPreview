package fastpreview.pixel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents a buffer of pixels, ideally stored in off-heap memory.
 */
public class PixelBuffer {
    private final int width;
    private final int height;
    private final PixelFormat format;
    private final ByteBuffer buffer;

    public PixelBuffer(int width, int height, PixelFormat format) {
        this.width = width;
        this.height = height;
        this.format = format;
        
        int bytesPerPixel = 4; // Assuming 32-bit formats
        int capacity = width * height * bytesPerPixel;
        
        // Allocate direct buffer (off-heap)
        this.buffer = ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public PixelFormat getFormat() {
        return format;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
    
    /**
     * Clears the buffer with a specific color (BGRA).
     */
    public void clear(int bgra) {
        buffer.rewind();
        while (buffer.hasRemaining()) {
            buffer.putInt(bgra);
        }
    }
}
