package fastpreview.benchmark;

import fastpreview.api.PreviewCodec;
import fastpreview.api.PreviewRecord;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class Benchmark {

    private List<PreviewRecord> sampleRecords;
    private byte[] sampleBinary;

    @Setup
    public void setup() {
        sampleRecords = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            sampleRecords.add(new PreviewRecord(
                    "C:\\Docs\\manual-page-" + i + ".pdf",
                    12 + (i % 5),
                    1920,
                    1080,
                    (i % 2 == 0) ? "NATIVE" : "PDFBOX",
                    true
            ));
        }
        sampleBinary = PreviewCodec.encode(sampleRecords);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public byte[] benchmarkEncode100PreviewRecords() {
        return PreviewCodec.encode(sampleRecords);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public List<PreviewRecord> benchmarkDecode100PreviewRecords() {
        return PreviewCodec.decode(sampleBinary);
    }
}
