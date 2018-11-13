package test.ru;

import java.io.*;

public class WriteFileTask {

    private InputStream is;
    private int bytesRead;
    private int size;
    private String fileName;

    public WriteFileTask(InputStream is, int size, String fileName) {
        this.is = is;
        this.size = size;
        this.fileName = fileName;
    }


    public Object write() {
        try {
            byte[] buffer = new byte[size];
            System.out.println("POST - "+"/v1/upload "+fileName + " "+size);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File("").getAbsolutePath()+File.separator+fileName));
            while ((bytesRead = is.read(buffer)) > 0) {
                bos.write(buffer, 0, bytesRead);
                System.out.println("POST - " + "write to " + fileName + " " + bytesRead);
            }
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

