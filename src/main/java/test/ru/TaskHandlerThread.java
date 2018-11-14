package test.ru;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.ru.channel.FileDataChannel;
import test.ru.channel.RequestDataChannel;

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
        RequestChannelMap requestChannelMap = RequestChannelMap.getInstance();
        FileChannelMap fileChannelMap = FileChannelMap.getInstance();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                for (Map.Entry<String, RequestDataChannel> entry : requestChannelMap.entrySet()) {
                    FileDataChannel currentFileChannel = fileChannelMap.get(entry.getKey());
                    if (currentFileChannel == null) {
                        currentFileChannel = new FileDataChannel(entry.getValue().getFile());
                        fileChannelMap.put(currentFileChannel.getFile().getFileName(), currentFileChannel);
                    }
                    if (currentFileChannel.tryLock()) {
                        RequestDataChannel requestDataChannel = entry.getValue();
                        if (requestDataChannel.tryLock()) {
                            byte[] buffer = null;
                            LOG.info("++++++++++++++++++ ThreadNum : {} lock ",number);
                            currentFileChannel.setOpenFile(false);
                            while ((buffer = requestDataChannel.pollFirst()) != null) {
                                requestDataChannel.incrementBlockCount();
                                currentFileChannel.writeData(buffer, number);
                                requestDataChannel.addWritedBytes(buffer.length);
                                if (currentFileChannel.getWritedBytes() == requestDataChannel.getFile().getSize()) {
                                    requestChannelMap.remove(requestDataChannel.getFile().getFileName());
                                    fileChannelMap.remove(currentFileChannel.getFile().getFileName());
                                    LOG.info("Success File Writed {} size {} ThreadNum {}",currentFileChannel.getFile().getFileName(), currentFileChannel.getWritedBytes(),number);
                                }
                            }
                            currentFileChannel.closeDestFile();
                            LOG.info("-------------- ThreadNum : {} unlock ",number);
                            requestDataChannel.unlock();
                        }
                        currentFileChannel.unlock();
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
