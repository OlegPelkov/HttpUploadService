package test.ru;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskContainer {

    public static class SingletonHolder {
        public static final TaskContainer INSTANCE = new TaskContainer();
    }

    public static TaskContainer getInstance() {
        return SingletonHolder.INSTANCE;
    }

    BlockingQueue<WriteFileTask> concurrentLinkedQueue = new LinkedBlockingQueue();

    public TaskContainer() {
    }

    public boolean add(WriteFileTask o) throws IllegalStateException {
        return concurrentLinkedQueue.add(o);
    }

    public WriteFileTask take() throws InterruptedException {
        return concurrentLinkedQueue.take();
    }
}
