package test.ru.channel;

import test.ru.fileAttributs.FileAttribute;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class DataChannel {

    protected final Lock lock = new ReentrantLock();
    protected final FileAttribute file;
    protected AtomicInteger countWrittenBytes = new AtomicInteger();
    protected AtomicInteger countWrittenBlocks = new AtomicInteger();
    protected AtomicLong timeDuration = new AtomicLong(0);

    public DataChannel(FileAttribute file) {
        this.file = file;
    }

    public abstract int getCountWrittenBytes();

    public abstract FileAttribute getFileAttribute();

    public abstract long getDuration();

}
