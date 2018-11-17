package test.ru.channel;

import test.ru.fileAttributs.FileAttribute;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Data channel between request and thread;
 * **/
public class RequestDataChannel extends DataChannel{

    private final Queue<byte[]> queue = new ConcurrentLinkedQueue();

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

    public void add(byte[] bytes) {
        queue.add(bytes);
    }

    public byte[] poll() {
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }
}
