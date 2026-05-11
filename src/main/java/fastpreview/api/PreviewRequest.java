package fastpreview.api;

import java.io.File;

/**
 * Parameters for a rendering request.
 */
public class PreviewRequest {
    private final File source;
    private final int width;
    private final int height;
    private final int dpi;
    private final int pageIndex; // For multi-page documents like PDF

    public PreviewRequest(File source, int width, int height) {
        this(source, width, height, 96, 0);
    }

    public PreviewRequest(File source, int width, int height, int dpi, int pageIndex) {
        this.source = source;
        this.width = width;
        this.height = height;
        this.dpi = dpi;
        this.pageIndex = pageIndex;
    }

    public File getSource() {
        return source;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDpi() {
        return dpi;
    }

    public int getPageIndex() {
        return pageIndex;
    }
}
