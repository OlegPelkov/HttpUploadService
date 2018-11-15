package test.ru;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.ru.channel.FileDataChannel;
import test.ru.channel.RequestDataChannel;
import test.ru.channelMaps.FileChannelMap;
import test.ru.channelMaps.RequestChannelMap;

import java.util.Map;

import static test.ru.utils.Utils.deleteOldFile;
import static test.ru.utils.Utils.isExist;

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
                        currentFileChannel = new FileDataChannel(entry.getValue().getFileAttribute());
                        String fileName = currentFileChannel.getFileAttribute().getFileName();
                        if(requestChannelMap.containsKey(fileName)) {
                            fileChannelMap.putIfAbsent(fileName, currentFileChannel);
                            if (isExist(fileName) && !deleteOldFile(fileName)) {
                                LOG.error("ThreadNum : {} can not delete old file {}", number, fileName);
                                continue;
                            }
                        } else {
                         continue;
                        }
                    }
                    if (currentFileChannel.tryLock()) {
                        try {
                            RequestDataChannel requestDataChannel = entry.getValue();
                            if (requestDataChannel.tryLock()) {
                                LOG.info("++++++++++++++++++ ThreadNum : {} lock ", number);
                                try {
                                    byte[] buffer = null;
                                    currentFileChannel.setOpenFile(false);
                                    while ((buffer = requestDataChannel.pollFirst()) != null) {
                                        requestDataChannel.incrementBlockCount();
                                        currentFileChannel.writeData(buffer, number);
                                        requestDataChannel.addWritedBytes(buffer.length);
                                        Thread.sleep(20);
                                        if (currentFileChannel.getCountWrittenBytes() == requestDataChannel.getFileAttribute().getSize()) {
                                            requestChannelMap.remove(requestDataChannel.getFileAttribute().getFileName());
                                            fileChannelMap.remove(currentFileChannel.getFileAttribute().getFileName());
                                            LOG.info("Success File Writed {} size {} ThreadNum {}", currentFileChannel.getFileAttribute().getFileName(), currentFileChannel.getCountWrittenBytes(), number);
                                        }
                                    }
                                    currentFileChannel.closeDestFile();
                                } finally {
                                    LOG.info("-------------- ThreadNum : {} unlock ", number);
                                    requestDataChannel.unlock();
                                }
                            }
                        } finally {
                            currentFileChannel.unlock();
                        }
                    }
                }
                Thread.sleep(10);
            } catch (Exception e) {
                LOG.error("Error {}", e);
                if(e instanceof InterruptedException){
                    Thread.currentThread().interrupt();
                }
            }
        }
        LOG.info("{}-{} stopped", TaskHandlerThread.class.getSimpleName(), number);
    }
}
