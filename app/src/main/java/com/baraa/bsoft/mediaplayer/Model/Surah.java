package com.baraa.bsoft.mediaplayer.Model;

/**
 * Created by baraa on 01/01/2018.
 */

public class Surah {
    private String title;
    private String url;
    private int number;

    public Surah(String title, String url, int number) {
        this.title = title;
        this.url = url;
        this.number = number;
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
}
