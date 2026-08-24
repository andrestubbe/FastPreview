package fastpreview;

import fastpreview.api.PreviewCodec;
import fastpreview.api.PreviewRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FastPreviewTest {

    @Test
    public void testPreviewCodecSerialization(@TempDir Path tempDir) throws IOException {
        List<PreviewRecord> records = List.of(
                new PreviewRecord("C:\\Docs\\architecture.pdf", 45, 1024, 768, "PDFBOX", true),
                new PreviewRecord("C:\\Source\\Demo.java", 2, 800, 600, "TEXT", true)
        );

        byte[] encoded = PreviewCodec.encode(records);
        assertNotNull(encoded);
        assertTrue(encoded.length >= 12);

        List<PreviewRecord> decoded = PreviewCodec.decode(encoded);
        assertEquals(2, decoded.size());
        assertEquals("C:\\Docs\\architecture.pdf", decoded.get(0).sourcePath());
        assertEquals(45, decoded.get(0).renderTimeMs());
        assertTrue(decoded.get(0).success());

        Path file = tempDir.resolve("previews.previewbin");
        PreviewCodec.writeToFile(file, records);
        assertTrue(file.toFile().exists());

        List<PreviewRecord> fromDisk = PreviewCodec.readFromFile(file);
        assertEquals(2, fromDisk.size());
    }
}
