package com.baraa.bsoft.mediaplayer.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DataStored extends RealmObject {
    @PrimaryKey
    private String key;
    private String localPath;


    public DataStored() {
    }

    public DataStored(String key, String localPath) {
        this();
        this.key = key;
        this.localPath = localPath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
