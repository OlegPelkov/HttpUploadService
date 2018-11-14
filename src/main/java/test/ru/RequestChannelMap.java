package test.ru;

import test.ru.channel.RequestDataChannel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RequestChannelMap {

    private Map<String, RequestDataChannel> requestChannelMap = new ConcurrentHashMap<>();

    public static class SingletonHolder {
        public static final RequestChannelMap INSTANCE = new RequestChannelMap();
    }

    public static RequestChannelMap getInstance() {
        return RequestChannelMap.SingletonHolder.INSTANCE;
    }

    public RequestDataChannel get(Object key) {
        return requestChannelMap.get(key);
    }

    public RequestDataChannel put(String key, RequestDataChannel value) {
        return requestChannelMap.put(key, value);
    }

    public RequestDataChannel remove(Object key) {
        return requestChannelMap.remove(key);
    }

    public Set<Map.Entry<String, RequestDataChannel>> entrySet() {
        return requestChannelMap.entrySet();
    }

}
