package com.datpv134.closeeyesandlisten.adapter;

import com.datpv134.closeeyesandlisten.model.MyNotification;
import com.datpv134.closeeyesandlisten.model.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IService {
    @GET("getMusic")
    Call<List<Song>> getSongs();

    @GET("getNotification")
    Call<List<MyNotification>> getNotifications();
}
