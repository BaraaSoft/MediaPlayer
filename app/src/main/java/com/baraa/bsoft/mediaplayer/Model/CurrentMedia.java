package com.baraa.bsoft.mediaplayer.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by baraa on 31/03/2018.
 */

public class CurrentMedia  extends RealmObject {

    @PrimaryKey
    private String key;

    private String surahkey;
    private String artistKey;
    private double progress;
    private int index;
    public CurrentMedia() {
    }

    public CurrentMedia(String surahkey, String artistKey,int index ,double progress,String key) {
        this();
        this.surahkey = surahkey;
        this.artistKey = artistKey;
        this.progress = progress;
        this.index = index;
        this.key = key;
    }

    public String getSurahkey() {
        return surahkey;
    }

    public void setSurahkey(String surahkey) {
        this.surahkey = surahkey;
    }

    public String getArtistKey() {
        return artistKey;
    }

    public void setArtistKey(String artistKey) {
        this.artistKey = artistKey;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
