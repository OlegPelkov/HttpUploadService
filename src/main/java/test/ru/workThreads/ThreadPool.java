package test.ru.workThreads;

import java.util.concurrent.*;

public class ThreadPool {

    public static class SingletonHolder {
        public static final ThreadPool INSTANCE = new ThreadPool();
    }

    public static ThreadPool getInstance() {
        return ThreadPool.SingletonHolder.INSTANCE;
    }

    private int threadsCount = 1;

    private ExecutorService threadPool = null;

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