package com.baraa.bsoft.mediaplayer.Model;

/**
 * Created by baraa on 21/03/2018.
 */

public class Artist {
    private String key;
    private int imageResourceId;
    private String name;

    public Artist(String key, int imageResourceId, String name) {
        this.key = key;
        this.imageResourceId = imageResourceId;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
