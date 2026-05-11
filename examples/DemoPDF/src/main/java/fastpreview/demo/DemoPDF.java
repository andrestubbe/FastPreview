package fastpreview.demo;

import fastpreview.api.FastPreview;
import fastpreview.api.PreviewRequest;
import fastpreview.api.PreviewResult;
import java.io.File;

public class DemoPDF {
    public static void main(String[] args) {
        System.out.println("FastPreview - PDF Demo");
        
        FastPreview preview = new FastPreview();
        PreviewRequest request = new PreviewRequest(new File("../assets/example.pdf"), 800, 600);
        
        PreviewResult result = preview.render(request);
        
        if (result.isSuccess()) {
            System.out.println("PDF Rendered successfully!");
            System.out.println("Dimensions: " + result.getPixelBuffer().getWidth() + "x" + result.getPixelBuffer().getHeight());
            System.out.println("Time: " + (result.getRenderTimeNanos() / 1_000_000.0) + " ms");
        } else {
            System.err.println("Error: " + result.getErrorMessage());
        }
    }
}
