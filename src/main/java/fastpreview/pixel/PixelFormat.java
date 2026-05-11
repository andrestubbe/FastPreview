package fastpreview.pixel;

/**
 * Supported pixel formats for rendering.
 */
public enum PixelFormat {
    /**
     * 32-bit BGRA format (Blue, Green, Red, Alpha).
     * Standard for Windows native APIs (DirectX, GDI+).
     */
    BGRA32,
    
    /**
     * 32-bit RGBA format.
     */
    RGBA32
}
