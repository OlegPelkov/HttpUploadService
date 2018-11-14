package test.ru.channel;

import test.ru.fileAttributs.FileAttribute;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Data channel between request and thread;
 * **/
public class RequestDataChannel extends DataChannel{

    private final Deque<byte[]> blockingQueue = new ConcurrentLinkedDeque();

    public RequestDataChannel(FileAttribute file) {
        super(file);
    }

    public int getCountWrittenBytes() {
        return countWrittenBytes.get();
    }

    public void addWritedBytes(int writedBytes) {
        this.countWrittenBytes.addAndGet(writedBytes);
    }

    @Override
    public long getDuration() {
        return timeDuration.get();
    }

    public int getWritedBlocks() {
        return countWrittenBlocks.get();
    }

    public void incrementBlockCount() {
        this.countWrittenBlocks.incrementAndGet();
    }

    public FileAttribute getFileAttribute() {
        return file;
    }

    public boolean tryLock() {
        return lock.tryLock();
    }

    public void unlock() {
        lock.unlock();
    }

    public void addLast(byte[] bytes) {
        blockingQueue.addLast(bytes);
    }

    public byte[] pollFirst() {
        return blockingQueue.pollFirst();
    }

    public int size() {
        return blockingQueue.size();
    }
}
