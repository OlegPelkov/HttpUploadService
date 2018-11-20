package test.ru.workThreads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.ru.channel.FileDataChannel;
import test.ru.channel.RequestDataChannel;
import test.ru.channelMaps.FileChannelMap;
import test.ru.channelMaps.FreeSpaceCounter;
import test.ru.channelMaps.RequestChannelMap;
import test.ru.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import static test.ru.utils.Utils.deleteOldFile;
import static test.ru.utils.Utils.isExist;

public class TaskHandlerThread implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(TaskHandlerThread.class);

    private final int number;

    private final Map<String, RandomAccessFile> fileMap = new HashMap();

    public TaskHandlerThread(int number) {
        this.number = number;
    }

    private DataBlock getDataBlock(RequestDataChannel requestDataChannel) {
        if (requestDataChannel.tryLock()) {
            try {
                int bytePoint = requestDataChannel.getNextBytePoint();
                byte[] buffer = null;
                if ((buffer = requestDataChannel.poll()) == null) {
                    return null;
                }
                return new DataBlock(buffer, bytePoint);
            } finally {
                requestDataChannel.unlock();
            }
        }
        return null;
    }

    private RandomAccessFile getFileWriter(String fileName) throws IOException {
        if (!fileMap.containsKey(fileName)) {
            File dir = new File(Utils.DIR_PATH);
            dir.mkdir();
            RandomAccessFile fileDest = new RandomAccessFile(new File("").getAbsolutePath() + Utils.DIR_NAME + File.separator + fileName, "rw");
            fileDest.skipBytes(0);
            fileMap.putIfAbsent(fileName, fileDest);
            LOG.debug("ThreadNum :{} open file {}", number, fileName);
        }
        return fileMap.get(fileName);
    }


    /**
    * Thread take dataBlock from requestDataChannel and write it to file by fileName at offset position
    * **/
    @Override
    public void run() {
        LOG.info("{}-{} started", TaskHandlerThread.class.getSimpleName(), number);
        RequestChannelMap requestChannelMap = RequestChannelMap.getInstance();
        FileChannelMap fileChannelMap = FileChannelMap.getInstance();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                for (Map.Entry<String, RequestDataChannel> entry : requestChannelMap.entrySet()) {
                    FileDataChannel currentFileChannel = fileChannelMap.get(entry.getKey());
                    if (currentFileChannel != null) {
                        RequestDataChannel requestDataChannel = entry.getValue();
                        DataBlock dataBlock;
                        while ((dataBlock = getDataBlock(requestDataChannel)) != null) {
                            LOG.debug("ThreadNum : {} point {}", number, dataBlock.getOffset());
                            RandomAccessFile fileDest = getFileWriter(requestDataChannel.getFileAttribute().getFileName());
                            fileDest.seek(dataBlock.getOffset());
                            fileDest.write(dataBlock.getData());
                            currentFileChannel.updateTime();
                            currentFileChannel.addWritedBytes(dataBlock.getData().length);
                            FreeSpaceCounter.getInstance().subtract(dataBlock.getData().length);
                            requestDataChannel.addWritedBytes(dataBlock.getData().length);
                            requestDataChannel.incrementBlockCount();
                            int block = currentFileChannel.incrementBlockCount();
                            LOG.debug("ThreadNum :{} write {} bytes in {} block to {} ", number, dataBlock.getData().length, block, requestDataChannel.getFileAttribute().getFileName());
                            Thread.sleep(10);
                        }
                        if (currentFileChannel.getCountWrittenBytes() == requestDataChannel.getFileAttribute().getSize()) {
                            RandomAccessFile fileDest = fileMap.get(requestDataChannel.getFileAttribute().getFileName());
                            requestChannelMap.remove(requestDataChannel.getFileAttribute().getFileName());
                            fileChannelMap.remove(currentFileChannel.getFileAttribute().getFileName());
                            fileDest.close();
                            LOG.info("File {} successfully written {} size {} ThreadNum {}", currentFileChannel.getFileAttribute().getFileName(), currentFileChannel.getCountWrittenBytes(), number);
                        }
                    }
                }
                Thread.sleep(10);
            } catch (Exception e) {
                LOG.error("Error {}", e);
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        LOG.info("{}-{} stopped", TaskHandlerThread.class.getSimpleName(), number);
    }
}
