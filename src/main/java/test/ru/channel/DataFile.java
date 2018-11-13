package test.ru.channel;

public class DataFile {

    private final String fileName;
    private final int size;

    public String getFileName() {
        return fileName;
    }

    public int getSize() {
        return size;
    }

    public DataFile(String fileName, int size) {
        this.fileName = fileName;
        this.size = size;
    }


}
