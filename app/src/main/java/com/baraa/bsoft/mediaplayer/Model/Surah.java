package com.baraa.bsoft.mediaplayer.Model;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by baraa on 01/01/2018.
 */

public class Surah extends RealmObject implements Serializable{

    @PrimaryKey
    private String key;
    private String artistKey;

    private String localPath;
    private String title;
    private String url;
    private int number;
    private boolean stored;
    private boolean isPlaying;

    private int progress;
    public Surah(){
        super();
    }

    public Surah(String title, String url, int number,String artistKey) {
        this();
        this.title = title;
        this.url = url;
        this.number = number;
        this.setKey(number+artistKey);
        this.progress = 0;
        this.stored = false;
        this.isPlaying = false;
        this.artistKey = artistKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getArtistKey() {
        return artistKey;
    }

    public void setArtistKey(String artistKey) {
        this.artistKey = artistKey;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
    public boolean isStored() {
        return stored;
    }

    public void setStored(boolean stored) {
        this.stored = stored;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
