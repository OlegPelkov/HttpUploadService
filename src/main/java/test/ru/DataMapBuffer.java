package test.ru;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.BiConsumer;

public class DataMapBuffer {

    private Map<String, LinkedBlockingDeque<byte[]>> dataMap = new ConcurrentHashMap<>();

    public static class SingletonHolder {
        public static final DataMapBuffer INSTANCE = new DataMapBuffer();
    }

    public static DataMapBuffer getInstance() {
        return DataMapBuffer.SingletonHolder.INSTANCE;
    }


    public LinkedBlockingDeque<byte[]> get(Object key) {
        return dataMap.get(key);
    }

    public LinkedBlockingDeque<byte[]> put(String key, LinkedBlockingDeque value) {
        return dataMap.put(key, value);
    }

    public LinkedBlockingDeque<byte[]> remove(Object key) {
        return dataMap.remove(key);
    }

    public Set<Map.Entry<String, LinkedBlockingDeque<byte[]>>> entrySet() {
        return dataMap.entrySet();
    }

    public void forEach(BiConsumer<? super String, ? super LinkedBlockingDeque<byte[]>> action) {
        dataMap.forEach(action);
    }
}
