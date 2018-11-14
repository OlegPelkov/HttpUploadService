package test.ru.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Data channel between thread and file;
 **/

public class FileDataChannel {

    private static final Logger LOG = LoggerFactory.getLogger(FileDataChannel.class);

    private AtomicBoolean openFile = new AtomicBoolean(false);
    private RandomAccessFile fileDest = null;
    private final DataFile sourceDatafile;
    private final Lock lock = new ReentrantLock();
    private AtomicInteger writedBytes = new AtomicInteger();
    private AtomicInteger writedBlocks = new AtomicInteger();

    public int getWritedBytes() {
        return writedBytes.get();
    }

    public FileDataChannel(DataFile file) {
        this.sourceDatafile = file;
    }

    public boolean isOpenFile() {
        return openFile.get();
    }

    public void setOpenFile(boolean newValue) {
        openFile.set(newValue);
    }

    public void lock() {
        lock.lock();
    }

    public boolean tryLock() {
        return lock.tryLock();
    }

    public void unlock() {
        lock.unlock();
    }

    public DataFile getFile() {
        return sourceDatafile;
    }

    public void closeDestFile() throws IOException {
        if (fileDest != null) {
            fileDest.close();
        }
        setOpenFile(false);
    }

    private synchronized void openFile(int number) throws IOException {
        LOG.debug("ThreadNum : {} open file {}", number, sourceDatafile.getFileName());
        fileDest = new RandomAccessFile(new File("").getAbsolutePath() + File.separator + sourceDatafile.getFileName(), "rw");
        fileDest.skipBytes((int) fileDest.length());
        openFile.set(true);
    }

    public synchronized void writeData(byte[] buffer, int number) throws IOException {
        if (!isOpenFile()) {
            openFile(number);
        }
        fileDest.write(buffer, 0, buffer.length);
        writedBytes.addAndGet(buffer.length);
        writedBlocks.incrementAndGet();
        LOG.debug("ThreadNum : {} write {} bytes in {} block to {}  all bytes - {}", number, buffer.length, writedBlocks, sourceDatafile.getFileName(), writedBytes.get());
    }
}


