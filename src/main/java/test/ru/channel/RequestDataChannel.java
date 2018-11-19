package test.ru.channel;

import test.ru.fileAttributs.FileAttribute;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Data channel between request and thread;
 * **/
public class RequestDataChannel extends DataChannel{

    private final Queue<byte[]> queue = new ConcurrentLinkedQueue();
    private AtomicInteger nextBytePoint = new AtomicInteger();

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

    public int incrementBlockCount() {
       return this.countWrittenBlocks.incrementAndGet();
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

    public synchronized byte[] poll() {
        byte[] bytes = queue.poll();
        if(bytes == null){
            return bytes;
        }
        nextBytePoint.addAndGet(bytes.length);
        return bytes;
    }

    public int getNextBytePoint() {
        return nextBytePoint.get();
    }

}
