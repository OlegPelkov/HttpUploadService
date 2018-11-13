package test.ru;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskContainer {

    private static final Logger LOG = LoggerFactory.getLogger(TaskContainer.class);

    public static class SingletonHolder {
        public static final TaskContainer INSTANCE = new TaskContainer();
    }

    public static TaskContainer getInstance() {
        return SingletonHolder.INSTANCE;
    }

    BlockingQueue<WriteFileTask> concurrentLinkedQueue = new LinkedBlockingQueue();

    public TaskContainer() {
    }

    public boolean add(WriteFileTask writeFileTask) throws IllegalStateException {
        LOG.info("+++++++++++++ add {}",writeFileTask);
        return concurrentLinkedQueue.add(writeFileTask);
    }

    public WriteFileTask take() throws InterruptedException {
        WriteFileTask task = concurrentLinkedQueue.take();
        LOG.info("------------ take {}",task);
        return task;
    }
}
