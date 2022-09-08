package com.zwy.ipmsg;

import com.zwy.ipmsg.beans.RecordItemEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class RecordSave {
    private static RecordSave instance;

    public static RecordSave getInstance() {
        if (instance == null) {
            instance = new RecordSave();
        }
        return instance;
    }

    private Map<String, Vector<RecordItemEntity>> recordMap;

    public RecordSave() {
        recordMap = new HashMap<>();
    }

    public Map<String, Vector<RecordItemEntity>> getRecordMap() {
        return recordMap;
    }

    public void setRecordMap(Map<String, Vector<RecordItemEntity>> recordMap) {
        this.recordMap = recordMap;
    }

    public void addRecord(String key, RecordItemEntity record) {
        recordMap.get(key).addElement(record);
    }

    public void addUser(String key) {
        recordMap.put(key, new Vector<>());
    }

    public void removeUser(String key) {
        recordMap.remove(key);
    }

    public Vector<RecordItemEntity> getRecords(String key) {
        return recordMap.get(key);
    }
}
