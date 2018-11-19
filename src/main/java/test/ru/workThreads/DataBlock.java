package test.ru.workThreads;

public class DataBlock {

    private byte[] data;
    private int offset;

    public DataBlock(byte[] data, int offset) {
        this.data = data;
        this.offset = offset;
    }

    public byte[] getData() {
        return data;
    }

    public int getOffset() {
        return offset;
    }
}
