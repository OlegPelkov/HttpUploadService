package test.ru;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class WriteFileTask {

    private static final Logger LOG = LoggerFactory.getLogger(TaskHandlerThread.class);

    private InputStream is;
    private String fileName;
    private int bytesWrited;
    private int size;
    private long startLoadTime;

    public WriteFileTask(InputStream is, int size, String fileName) {
        this.is = is;
        this.size = size;
        this.fileName = fileName;
    }

    public Object write() {
        LOG.info("Start write " + fileName + " " + size);
        BufferedOutputStream bos = null;
        try {
            startLoadTime = System.currentTimeMillis();
            byte[] buffer = new byte[size];
            int bytesRead;
            File file = new File(new File("").getAbsolutePath()+File.separator+fileName);
            if(file.exists() && !file.canWrite()){
                LOG.info("Can not write " + fileName);
                return null;
            }
            bos = new BufferedOutputStream(new FileOutputStream(file));
            while ((bytesRead = is.read(buffer)) > 0) {
                bos.write(buffer, 0, bytesRead);
                bytesWrited += bytesRead;
                LOG.info("Write " + fileName + " " + bytesRead);
            }
            bos.close();
        } catch (Exception e) {
            if(bos!=null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    LOG.info("Error write {}",e1);
                }
            }
            LOG.info("Error write {}",e);
        }
        LOG.info("Success write " + fileName + " " + size);
        return null;
    }

    public Long getTimeDurationOfUpload(){
        long durationUploadTime = System.currentTimeMillis() - startLoadTime;
        return durationUploadTime;
    }

    public int getBytesWrited(){
        return bytesWrited;
    }

    @Override
    public String toString() {
        return "WriteFileTask{" +
                "size=" + size +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}

