package test.ru;

import test.ru.channel.DataChannel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.BiConsumer;

public class DataMapBuffer {

    private Map<String, DataChannel> dataMap = new ConcurrentHashMap<>();

    public static class SingletonHolder {
        public static final DataMapBuffer INSTANCE = new DataMapBuffer();
    }

    public static DataMapBuffer getInstance() {
        return DataMapBuffer.SingletonHolder.INSTANCE;
    }

    public DataChannel get(Object key) {
        return dataMap.get(key);
    }

    public DataChannel put(String key, DataChannel value) {
        return dataMap.put(key, value);
    }

    public DataChannel remove(Object key) {
        return dataMap.remove(key);
    }

    public Set<Map.Entry<String, DataChannel>> entrySet() {
        return dataMap.entrySet();
    }

}
