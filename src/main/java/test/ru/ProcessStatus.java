package test.ru;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessStatus {

    private Map<String, WriteFileTask> statuskMap = new ConcurrentHashMap<>();

    public int size() {
        return statuskMap.size();
    }

    public boolean containsKey(String key) {
        return statuskMap.containsKey(key);
    }

    public WriteFileTask get(String key) {
        return statuskMap.get(key);
    }

    public WriteFileTask put(String key, WriteFileTask value) {
        return statuskMap.put(key, value);
    }

    public WriteFileTask remove(String key) {
        return statuskMap.remove(key);
    }
}
