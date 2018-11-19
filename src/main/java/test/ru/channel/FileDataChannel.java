package test.ru.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.ru.fileAttributs.FileAttribute;
import test.ru.utils.Utils;
import test.ru.workThreads.DataBlock;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Data channel between thread and file;
 **/

public class FileDataChannel extends DataChannel {

    private static final Logger LOG = LoggerFactory.getLogger(FileDataChannel.class);

    private AtomicBoolean openFile = new AtomicBoolean(false);
    private RandomAccessFile fileDest = null;
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

    public boolean isOpenFile() {
        return openFile.get();
    }

    public void setOpenFile(boolean newValue) {
        openFile.set(newValue);
    }

    public boolean tryLock() {
        return lock.tryLock();
    }

    public void unlock() {
        lock.unlock();
    }

    public FileAttribute getFileAttribute() {
        return file;
    }

    public void closeDestFile() throws IOException {
        if (fileDest != null) {
            fileDest.close();
        }
        setOpenFile(false);
    }

    public void updateTime() throws IOException {
        timeDuration.set(System.currentTimeMillis()-timeStartUpload);
    }

}


