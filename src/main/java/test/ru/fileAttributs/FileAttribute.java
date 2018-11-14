package test.ru.fileAttributs;

public class FileAttribute {

    private String fileName;
    private final String id;
    private final int size;

    public String getFileName() {
        return fileName;
    }

    public int getSize() {
        return size;
    }

    public String getId() {
        return fileName + id;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public FileAttribute(String fileName, String id, int size) {
        this.fileName = fileName;
        this.id = id;
        this.size = size;
    }

}
