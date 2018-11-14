package test.ru.utils;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileNameIncrementService {

    private Map<String, Integer> fileNames = new ConcurrentHashMap<>();

    public static class SingletonHolder {
        public static final FileNameIncrementService INSTANCE = new FileNameIncrementService();
    }

    public static FileNameIncrementService getInstance() {
        return FileNameIncrementService.SingletonHolder.INSTANCE;
    }


}
