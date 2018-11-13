package test.ru;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

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
                for(Map.Entry<String, LinkedBlockingDeque<byte[]>> entry : DataMapBuffer.getInstance().entrySet()) {
                    String fileName = entry.getKey();
                    LinkedBlockingDeque<byte[]> is = entry.getValue();
                    byte[] buffer = null;
                    int bytesCountReaded = 0;
                    //File file = new File(new File("").getAbsolutePath()+File.separator+fileName);
                    if ((buffer = is.getFirst()) != null) {
                        RandomAccessFile file = new RandomAccessFile(new File("").getAbsolutePath() + File.separator + fileName, "rw");
                        long i = file.length();
                        file.skipBytes((int) file.length());
                        file.write(buffer, 0, buffer.length);
                        bytesCountReaded += buffer.length;
                        LOG.info("Write " + fileName + " " + buffer.length);
                        file.close();
                        LOG.info("Writed " + fileName + " " + bytesCountReaded);
                        if (file.length() == 5312178) {
                            DataMapBuffer.getInstance().remove(fileName);
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("Error {}", e);
            }
        }

        LOG.info("{}-{} stopped", TaskHandlerThread.class.getSimpleName(), number);
    }
}
