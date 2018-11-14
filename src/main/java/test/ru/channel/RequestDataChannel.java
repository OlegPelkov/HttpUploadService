package test.ru.channel;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Data channel between request and thread;
 * **/
public class RequestDataChannel {

    private final Deque<byte[]> blockingQueue = new ConcurrentLinkedDeque();
    private final Lock lock = new ReentrantLock();
    private final DataFile file;
    private AtomicInteger writedBytes = new AtomicInteger();
    private AtomicInteger writedBlocks = new AtomicInteger();

    public int getWritedBytes() {
        return writedBytes.get();
    }

    public void addWritedBytes(int writedBytes) {
        this.writedBytes.addAndGet(writedBytes);
    }

    public int getWritedBlocks() {
        return writedBlocks.get();
    }

    public void incrementBlockCount() {
        this.writedBlocks.incrementAndGet();
    }

    public RequestDataChannel(DataFile file) {
        this.file = file;
    }

    public DataFile getFile() {
        return file;
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
