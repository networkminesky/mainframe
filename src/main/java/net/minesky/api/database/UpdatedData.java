package net.minesky.api.database;

import java.util.HashMap;

public class UpdatedData {

    private HashMap<String, Object> list;

    public UpdatedData() {
        list = new HashMap<>();
    }

    public UpdatedData(String key, Object value) {
        list = new HashMap<>();
        add(key, value);
    }

    public void add(String key, Object value) {
        HashMap<String, String> newHash = new HashMap<>();
        list.put(key, value);
    }

    public HashMap<String, Object> getList() {
        return this.list;
    }

}
