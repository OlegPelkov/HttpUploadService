package test.ru.channelMaps;

import java.util.concurrent.atomic.AtomicLong;

public class FreeSpaceCounter {

    public static class SingletonHolder {
        public static final FreeSpaceCounter INSTANCE = new FreeSpaceCounter();
    }

    public static FreeSpaceCounter getInstance() {
        return FreeSpaceCounter.SingletonHolder.INSTANCE;
    }

    public long FREE_SPACE = 1000080*20;

    private AtomicLong usedSpace =  new AtomicLong(0);

    public long addAndGet(long delta) {
        return usedSpace.addAndGet(delta);
    }

    public boolean isHaveFreeSpace() {
        return usedSpace.get()< FREE_SPACE;
    }

    public void subtract(long delta) {
        while (!usedSpace.compareAndSet(usedSpace.get(), usedSpace.get()-delta)){}
    }

}
