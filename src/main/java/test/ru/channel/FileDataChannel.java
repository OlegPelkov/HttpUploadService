package test.ru.channel;

import test.ru.fileAttributs.FileAttribute;
import java.io.IOException;
/**
 * Data channel between thread and file;
 **/

public class FileDataChannel extends DataChannel {

    private final long timeStartUpload;

    public FileDataChannel(FileAttribute file) {
        super(file);
        timeStartUpload = System.currentTimeMillis();
    }

    public int getCountWrittenBytes() {
        return countWrittenBytes.get();
    }

    public void addWritedBytes(int writedBytes) {
        this.countWrittenBytes.addAndGet(writedBytes);
    }

    public int incrementBlockCount() {
        return this.countWrittenBlocks.incrementAndGet();
    }

    @Override
    public long getDuration() {
        return timeDuration.get();
    }

    public FileAttribute getFileAttribute() {
        return file;
    }

    public void updateTime() throws IOException {
        timeDuration.set(System.currentTimeMillis() - timeStartUpload);
    }

}


