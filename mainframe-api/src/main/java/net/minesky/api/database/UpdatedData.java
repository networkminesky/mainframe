package net.minesky.api.database;

import java.util.HashMap;

public class UpdatedData {

    private final HashMap<String, Object> list = new HashMap<>();

    public UpdatedData() {
    }

    public UpdatedData(String key, Object value) {
        add(key, value);
    }

    public void add(String key, Object value) {
        list.put(key, value);
    }

    public HashMap<String, Object> getList() {
        return list;
    }
}
