package test.ru;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.ru.channel.DataChannel;

import java.io.*;
import java.util.Map;

public class TaskHandlerThread implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(TaskHandlerThread.class);

    private int number;

    public TaskHandlerThread(int number) {
        this.number = number;
    }

    @Override
    public void run() {
        LOG.info("{}-{} started", TaskHandlerThread.class.getSimpleName(), number);
        DataMapBuffer dataMapBuffer = DataMapBuffer.getInstance();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                for(Map.Entry<String, DataChannel> entry : DataMapBuffer.getInstance().entrySet()) {
                    DataChannel dataChannel = entry.getValue();
                    byte[] buffer = null;
                    RandomAccessFile file = null;
                    if (dataChannel.tryLock()) {
                        LOG.info("++++++++++++++++++ ThreadNum : "+number+" lock");
                        boolean openFile = false;
                        while ((buffer = dataChannel.pollFirst()) != null) {
                            dataChannel.incBlocks();
                            LOG.info("ThreadNum : "+number+" Write " + dataChannel.getFile().getFileName() + " " + buffer.length + " ");
                            if(!openFile) {
                                LOG.info("ThreadNum : "+number+" Write openFile" + dataChannel.getFile().getFileName() + " ");
                                file = new RandomAccessFile(new File("").getAbsolutePath() + File.separator + dataChannel.getFile().getFileName(), "rw");
                                file.skipBytes((int) file.length());
                                openFile = true;
                            }
                            file.write(buffer, 0, buffer.length);
                            dataChannel.addWritedBytes(buffer.length);
                            LOG.info("ThreadNum : "+number+" Writed " + dataChannel.getFile().getFileName() + " " + dataChannel.getWritedBytes() + "  block: "+dataChannel.getWritedBlocks());
                            if (file.length() == dataChannel.getFile().getSize()) {
                                DataMapBuffer.getInstance().remove(dataChannel.getFile().getFileName());
                            }
                        }
                        if(file!=null) {
                            file.close();
                        }
                        LOG.info("-------------- ThreadNum : "+number+" unlock");
                        dataChannel.unlock();
                    }
                     Thread.sleep(20);
                }
            } catch (Exception e) {
                LOG.error("Error {}", e);
            }
        }

        LOG.info("{}-{} stopped", TaskHandlerThread.class.getSimpleName(), number);
    }
}
