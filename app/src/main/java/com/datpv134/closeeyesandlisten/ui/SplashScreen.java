package com.datpv134.closeeyesandlisten.ui;

import static com.datpv134.closeeyesandlisten.service.MyApplication.lofiChillList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.loveList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.remixList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.songList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.songListInHome;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.adapter.APIClientPM;
import com.datpv134.closeeyesandlisten.databinding.ActivitySplashScreenBinding;
import com.datpv134.closeeyesandlisten.model.MyNotification;
import com.datpv134.closeeyesandlisten.model.Song;
import com.datpv134.closeeyesandlisten.service.MyApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {
    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);

        Call<List<Song>> call = APIClientPM.getInstance().getSongs();

        call.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songList = response.body();
                songListInHome = songList;

                for (Song song : songList) {
                    if (Objects.equals(song.getPlayList(), "Lofi chill")) {
                        lofiChillList.add(song);
                    } else if (Objects.equals(song.getPlayList(), "Remix")) {
                        remixList.add(song);
                    } else if (Objects.equals(song.getPlayList(), "Love")) {
                        loveList.add(song);
                    }
                }

                Call<List<MyNotification>> callNotification = APIClientPM.getInstance().getNotifications();

                callNotification.enqueue(new Callback<List<MyNotification>>() {
                    @Override
                    public void onResponse(Call<List<MyNotification>> call, Response<List<MyNotification>> response) {
                        List<MyNotification> notifications = new ArrayList<MyNotification>();
                        notifications = response.body();
                        ((MyApplication) getApplication()).setMyNotifications(notifications);

                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<List<MyNotification>> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {

            }
        });
    }
}