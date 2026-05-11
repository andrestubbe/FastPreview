package fastpreview.demo;

import fastpreview.api.FastPreview;
import fastpreview.api.PreviewRequest;
import fastpreview.api.PreviewResult;
import java.io.File;

public class DemoScreenshot {
    public static void main(String[] args) {
        System.out.println("FastPreview - Screenshot Demo");
        
        FastPreview preview = new FastPreview();
        // Special case: source is interpreted as a "capture command" or window handle
        PreviewRequest request = new PreviewRequest(new File("SCREEN_PRIMARY"), 1920, 1080);
        
        PreviewResult result = preview.render(request);
        
        if (result.isSuccess()) {
            System.out.println("Screenshot captured successfully!");
            System.out.println("Dimensions: " + result.getPixelBuffer().getWidth() + "x" + result.getPixelBuffer().getHeight());
            System.out.println("Time: " + (result.getRenderTimeNanos() / 1_000_000.0) + " ms");
        } else {
            System.err.println("Error: " + result.getErrorMessage());
        }
    }
}
