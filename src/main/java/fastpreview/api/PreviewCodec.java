package fastpreview.api;

import fastfileformat.BinaryHeader;
import fastfileformat.BinaryReader;
import fastfileformat.BinaryWriter;
import fastfileformat.FastFileFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * High-speed binary serializer and stream decoder for cached document preview records (.previewbin).
 * Built on top of FastFileFormat and FastBinary VarInt compression.
 */
public final class PreviewCodec {
    /**
     * Payload type identifier for FastJava Document Preview Logs (0x0009).
     */
    public static final short PAYLOAD_TYPE_PREVIEWBIN = 0x0009;

    private PreviewCodec() {}

    /**
     * Encodes a list of preview records into a compressed FastFileFormat binary byte array.
     */
    public static byte[] encode(List<PreviewRecord> records) {
        if (records == null || records.isEmpty()) {
            BinaryWriter finalWriter = FastFileFormat.binaryWriter(12);
            finalWriter.writeHeader(FastFileFormat.DEFAULT_MAGIC, FastFileFormat.DEFAULT_VERSION, PAYLOAD_TYPE_PREVIEWBIN, 0);
            return finalWriter.toByteArray();
        }

        BinaryWriter payloadWriter = FastFileFormat.binaryWriter(records.size() * 32);
        payloadWriter.writeVarInt(records.size());

        for (PreviewRecord r : records) {
            payloadWriter.writeString(r.sourcePath() != null ? r.sourcePath() : "");
            payloadWriter.writeVarLong(r.renderTimeMs());
            payloadWriter.writeVarInt(r.width());
            payloadWriter.writeVarInt(r.height());
            payloadWriter.writeString(r.backend() != null ? r.backend() : "");
            payloadWriter.writeByte((byte) (r.success() ? 1 : 0));
        }

        byte[] payload = payloadWriter.toByteArray();

        BinaryWriter finalWriter = FastFileFormat.binaryWriter(12 + payload.length);
        finalWriter.writeHeader(
                FastFileFormat.DEFAULT_MAGIC,
                FastFileFormat.DEFAULT_VERSION,
                PAYLOAD_TYPE_PREVIEWBIN,
                payload.length
        );
        finalWriter.writeBytes(payload);
        return finalWriter.toByteArray();
    }

    /**
     * Decodes a .previewbin binary payload into a list of PreviewRecord instances.
     */
    public static List<PreviewRecord> decode(byte[] bytes) {
        if (bytes == null || bytes.length < 12) {
            return Collections.emptyList();
        }

        BinaryReader reader = FastFileFormat.binaryReader(bytes);
        BinaryHeader header = reader.readHeader();

        if (header.getMagic() != FastFileFormat.DEFAULT_MAGIC) {
            throw new IllegalArgumentException("Invalid FastFileFormat magic header: " + Integer.toHexString(header.getMagic()));
        }
        if (header.getPayloadType() != PAYLOAD_TYPE_PREVIEWBIN) {
            throw new IllegalArgumentException("Unexpected payload type for Previewbin: " + header.getPayloadType());
        }
        if (header.getPayloadLength() == 0) {
            return Collections.emptyList();
        }

        int count = reader.readVarInt();
        List<PreviewRecord> list = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            String path = reader.readString();
            long time = reader.readVarLong();
            int w = reader.readVarInt();
            int h = reader.readVarInt();
            String backend = reader.readString();
            boolean success = (reader.readByte() & 0xFF) == 1;

            list.add(new PreviewRecord(path, time, w, h, backend, success));
        }
        return Collections.unmodifiableList(list);
    }

    public static void writeToFile(Path path, List<PreviewRecord> records) throws IOException {
        byte[] bytes = encode(records);
        Files.write(path, bytes);
    }

    public static List<PreviewRecord> readFromFile(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return decode(bytes);
    }
}
