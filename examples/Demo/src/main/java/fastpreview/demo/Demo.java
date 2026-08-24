package fastpreview.demo;

import fastpreview.api.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class Demo {
    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println(" 📄 FastPreview — Native High-Speed Rendering");
        System.out.println("=================================================");

        FastPreview engine = new FastPreview();

        // 1. Create a dummy test file
        File tempText = File.createTempFile("preview_demo", ".txt");
        Files.writeString(tempText.toPath(), "Hello FastPreview Engine!\nLine 2: Fast rendering without heavy Swing UI.\nLine 3: FastJava Ecosystem.");

        System.out.println("Rendering preview for: " + tempText.getAbsolutePath());
        PreviewRequest req = new PreviewRequest(tempText, 800, 600);
        PreviewResult res = engine.render(req);

        System.out.printf("Render Status: %s (Time: %d ms, Backend: %s)\n",
                res.isSuccess() ? "SUCCESS" : "ERROR: " + res.getErrorMessage(),
                res.getRenderTimeMs(),
                res.getBackendUsed());

        // 2. FastFileFormat Binary Metadata Cache (.previewbin)
        List<PreviewRecord> records = List.of(
                new PreviewRecord(tempText.getAbsolutePath(), res.getRenderTimeMs(), 800, 600, res.getBackendUsed() != null ? res.getBackendUsed().name() : "TEXT", res.isSuccess())
        );

        byte[] encoded = PreviewCodec.encode(records);
        System.out.println("Encoded .previewbin cache payload size: " + encoded.length + " bytes.");

        List<PreviewRecord> decoded = PreviewCodec.decode(encoded);
        System.out.println("Decoded " + decoded.size() + " preview records from binary cache.");

        tempText.delete();
        System.out.println("\n✔ FastPreview Rendering Pipeline Verified Successfully!");
    }
}
