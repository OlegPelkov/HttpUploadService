package test.ru.channelMaps;

public class FreeSpaceCounter {

    public static class SingletonHolder {
        public static final FreeSpaceCounter INSTANCE = new FreeSpaceCounter();
    }

    public static FreeSpaceCounter getInstance() {
        return FreeSpaceCounter.SingletonHolder.INSTANCE;
    }

    public long FREE_SPACE = 100241388*20;

    private volatile long usedSpace = 0L;

    public synchronized long add(long delta) {
        return usedSpace+=delta;
    }

    public synchronized boolean isHaveFreeSpace() {
        return usedSpace< FREE_SPACE;
    }

    public synchronized void subtract(long delta) {
        usedSpace-=delta;
    }

}
