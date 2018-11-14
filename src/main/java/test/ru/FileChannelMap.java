package test.ru;

import test.ru.channel.FileDataChannel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FileChannelMap {

    private Map<String, FileDataChannel> fileChannelMap = new ConcurrentHashMap<>();

    public static class SingletonHolder {
        public static final FileChannelMap INSTANCE = new FileChannelMap();
    }

    public static FileChannelMap getInstance() {
        return FileChannelMap.SingletonHolder.INSTANCE;
    }

    public FileDataChannel get(Object key) {
        return fileChannelMap.get(key);
    }

    public FileDataChannel put(String key, FileDataChannel value) {
        return fileChannelMap.put(key, value);
    }

    public FileDataChannel remove(Object key) {
        return fileChannelMap.remove(key);
    }

    public Set<Map.Entry<String, FileDataChannel>> entrySet() {
        return fileChannelMap.entrySet();
    }

}
