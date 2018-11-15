package testUtils;

import java.util.Arrays;

public class TestFileCreator {

   // private static final int MAX_FILE_SIZE = 52428800;

    private static final int MAX_FILE_SIZE = 1000800;

    public static byte[] getTestData(){
        byte[] data = new byte[MAX_FILE_SIZE];
        byte b = 32;
        Arrays.fill(data, b);
        return data;
    }
}
