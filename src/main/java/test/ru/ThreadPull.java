package test.ru;

import java.util.concurrent.*;

public class ThreadPull {

    public static class SingletonHolder {
        public static final ThreadPull INSTANCE = new ThreadPull();
    }

    public static ThreadPull getInstance() {
        return ThreadPull.SingletonHolder.INSTANCE;
    }

    private ExecutorService threadPool = null;
    private int threadsCount = 8;

    public void setThreadsCount(int threadsCount) {
        this.threadsCount = threadsCount;
    }

    public void start() throws ExecutionException, InterruptedException {
        threadPool = Executors.newFixedThreadPool(threadsCount);
        for(int i=0;i< threadsCount;i++) {
            threadPool.submit(new TaskHandlerThread(i));
        }
    }

    public void stop(){
        threadPool.shutdown();
    }
}