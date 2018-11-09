package test.ru;

import java.io.File;
import java.io.FileOutputStream;

public class FileInfo {
    // имя файла
    public String filename;
    // указывает индекс начала
    //файла в массиве byte[] data
    public int start_index;
    // указывает индекс конца
    // файла в массиве byte[] data
    public int last_index;

    public FileInfo
            (String filename, int start_index, int last_index) {
        this.filename = filename;
        this.start_index = start_index;
        this.last_index = last_index;
    }

    // запись файла на диск
    public void SaveFile(byte[] data, String directory) {
        // если файл, то записываем на диск.
        if (!filename.equals("NO_FILE")) {
            File f = new File(directory + filename);
            try {
                FileOutputStream fos = new FileOutputStream(f);
                int length = last_index - start_index;
                fos.write(data, start_index, length);
                fos.close();

            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
    }
}