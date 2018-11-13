package test.ru;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskHandlerThread implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(TaskHandlerThread.class);

    private int number;

    public TaskHandlerThread(int number) {
        this.number = number;
    }

    @Override
    public void run() {
        LOG.info("{}-{} started", TaskHandlerThread.class.getSimpleName(), number);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                WriteFileTask task = TaskContainer.getInstance().take();
                if (task != null) {
                    task.write();
                }
            } catch (Exception e) {
                LOG.error("Error {}", e);
            }
        }

        LOG.info("{}-{} stopped", TaskHandlerThread.class.getSimpleName(), number);
    }
}
