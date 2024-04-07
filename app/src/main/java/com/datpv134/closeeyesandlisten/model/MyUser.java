package com.datpv134.closeeyesandlisten.model;

import java.util.List;

public class MyUser {
    private String name;
    private String email;
    private String password;
    private String profileImg;
    private String favoriteSong;
    private String playList;

    public MyUser(String name, String email, String password, String profileImg, String favoriteSong, String playList) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileImg = profileImg;
        this.favoriteSong = favoriteSong;
        this.playList = playList;
    }

    public MyUser() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getFavoriteSong() {
        return favoriteSong;
    }

    public void setFavoriteSong(String favoriteSong) {
        this.favoriteSong = favoriteSong;
    }

    public String getPlayList() {
        return playList;
    }

    public void setPlayList(String playList) {
        this.playList = playList;
    }
}
