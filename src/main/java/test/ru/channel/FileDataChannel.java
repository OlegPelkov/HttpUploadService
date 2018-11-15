package test.ru.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.ru.fileAttributs.FileAttribute;

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

    private synchronized void openFile(int number) throws IOException {
        LOG.debug("ThreadNum :{} open file {}", number, file.getFileName());
        fileDest = new RandomAccessFile(new File("").getAbsolutePath() + File.separator + file.getFileName(), "rw");
        fileDest.skipBytes((int) fileDest.length());
        openFile.set(true);
    }

    public synchronized void writeData(byte[] buffer, int number) throws IOException {
        if (!isOpenFile()) {
            openFile(number);
        }
        fileDest.write(buffer, 0, buffer.length);
        countWrittenBytes.addAndGet(buffer.length);
        countWrittenBlocks.incrementAndGet();
        timeDuration.set(System.currentTimeMillis()-timeStartUpload);
        LOG.debug("ThreadNum :{} write {} bytes in {} block to {} all bytes writed - {}", number, buffer.length, countWrittenBlocks, file.getFileName(), countWrittenBytes.get());
    }

}


