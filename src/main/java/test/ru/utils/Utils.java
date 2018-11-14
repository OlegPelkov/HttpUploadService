package test.ru.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String getFileNameWithTimeStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy M dd HH-mm-ss");
        return "-<" + dateFormat.format(new Date()).replace(" ", "_") + ">";
    }

    public static String getFileUploadDurationString(String id, long duration) {
        StringBuilder sb = new StringBuilder();
        long seconds = duration / 1000 % 60;
        long millis = duration % 1000 / 100;
        sb.append("upload_duration{id=”");
        sb.append(id);
        sb.append("”} ");
        sb.append(seconds);
        sb.append(".");
        sb.append(millis);
        return sb.toString();
    }

    public static boolean deleteOldFile(String fileName) {
        File file = new File(new File("").getAbsolutePath() + File.separator + fileName);
        return file.delete();
    }

    public static boolean isExist(String fileName){
        File file = new File(new File("").getAbsolutePath() + File.separator + fileName);
        return file.exists();
    }

}

