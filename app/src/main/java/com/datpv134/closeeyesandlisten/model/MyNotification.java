package com.datpv134.closeeyesandlisten.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyNotification {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("src")
    @Expose
    private String src;
    @SerializedName("updateTime")
    @Expose
    private String updateTime;

    public MyNotification(String id, String name, String author, String image, String src, String updateTime) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.image = image;
        this.src = src;
        this.updateTime = updateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
