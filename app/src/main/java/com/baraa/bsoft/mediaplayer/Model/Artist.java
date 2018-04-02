package com.baraa.bsoft.mediaplayer.Model;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by baraa on 21/03/2018.
 */

public class Artist extends RealmObject implements Serializable {
    @PrimaryKey
    private String key;
    private int imageResourceId;
    private String name;
    private String nameArabic;
    private String basUrl1;
    private String basUrl2;


    public Artist() {
    }

    public Artist(String key, int imageResourceId, String name) {
        this();
        this.key = key;
        this.imageResourceId = imageResourceId;
        this.name = name;
    }

    public Artist(String key, int imageResourceId, String name,String url1) {
        this();
        this.key = key;
        this.imageResourceId = imageResourceId;
        this.name = name;
        this.basUrl1 = url1;
    }
    public Artist(String key, int imageResourceId, String name,String url1,String url2) {
        this();
        this.key = key;
        this.imageResourceId = imageResourceId;
        this.name = name;
        this.basUrl1 = url1;
        this.basUrl2 = url2;
    }


    public Artist(String key, int imageResourceId, String name,String url1,String url2,String nameAr) {
        this(key,imageResourceId,name,url1,url2);
        this.nameArabic = nameAr;
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
    public String getBasUrl1() {
        return basUrl1;
    }

    public void setBasUrl1(String basUrl1) {
        this.basUrl1 = basUrl1;
    }

    public String getBasUrl2() {
        return basUrl2;
    }

    public void setBasUrl2(String basUrl2) {
        this.basUrl2 = basUrl2;
    }

    public String getNameArabic() {
        return nameArabic;
    }

    public void setNameArabic(String nameArabic) {
        this.nameArabic = nameArabic;
    }
}
