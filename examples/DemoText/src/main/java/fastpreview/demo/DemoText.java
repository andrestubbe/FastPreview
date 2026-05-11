package fastpreview.demo;

import fastpreview.api.FastPreview;
import fastpreview.api.PreviewRequest;
import fastpreview.api.PreviewResult;
import java.io.File;

public class DemoText {
    public static void main(String[] args) {
        System.out.println("FastPreview - Text Demo");
        
        FastPreview preview = new FastPreview();
        PreviewRequest request = new PreviewRequest(new File("LICENSE"), 800, 600);
        
        PreviewResult result = preview.render(request);
        
        if (result.isSuccess()) {
            System.out.println("Text Rendered successfully!");
            System.out.println("Dimensions: " + result.getPixelBuffer().getWidth() + "x" + result.getPixelBuffer().getHeight());
            System.out.println("Time: " + (result.getRenderTimeNanos() / 1_000_000.0) + " ms");
        } else {
            System.err.println("Error: " + result.getErrorMessage());
        }
    }
}
