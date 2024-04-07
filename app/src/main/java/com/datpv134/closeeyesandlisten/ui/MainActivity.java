package com.datpv134.closeeyesandlisten.ui;

import static com.datpv134.closeeyesandlisten.service.MyApplication.CHANNEL_ID;
import static com.datpv134.closeeyesandlisten.service.MyApplication.isCallInHomeReady;
import static com.datpv134.closeeyesandlisten.service.MyApplication.isPushNotifi;
import static com.datpv134.closeeyesandlisten.service.MyApplication.isRunning;
import static com.datpv134.closeeyesandlisten.service.MyApplication.lofiChillList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.loveList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.mediaPlayer;
import static com.datpv134.closeeyesandlisten.service.MyApplication.remixList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.songList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.songListInHome;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.adapter.APIClientPM;
import com.datpv134.closeeyesandlisten.databinding.ActivityMainBinding;
import com.datpv134.closeeyesandlisten.model.MyNotification;
import com.datpv134.closeeyesandlisten.model.Song;
import com.datpv134.closeeyesandlisten.service.MyApplication;
import com.datpv134.closeeyesandlisten.service.MyService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements Serializable {
    ActivityMainBinding binding;
    boolean isOpenNotifi = false;
    boolean isFavFragment = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        isOpenNotifi = getIntent().getBooleanExtra("openNotifi", false);

        if (isOpenNotifi) {
            binding.vChat.setVisibility(View.GONE);
            getFragment(NotificationFragment.newInstance());
        } else {
            binding.vChat.setVisibility(View.GONE);
            getFragment(HomeFragmet.newInstance());
        }

        onClickNavigationButton();

        binding.vChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), InUpdatingActivity.class));
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

    }

    void onClickNavigationButton() {
        binding.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mediaPlayer.isPlaying()) {
                    getFragment(HomeFragmet.newInstance());
                    changeButton(1);
                } else {
                    getFragment(HomeFragmet.newInstance());
                    changeButton(1);
                }
            }
        });

        binding.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragment(FavoriteFragment.newInstance());
                changeButton(2);
            }
        });

        binding.btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragment(ProfileFragment.newInstance());
                changeButton(3);
            }
        });
    }

    void changeButton(int id) {
        if (id == 1) {
            binding.vChat.setVisibility(View.GONE);
            binding.btnHome.setImageResource(R.drawable.home_white);
        } else {
            binding.btnHome.setImageResource(R.drawable.icon_home);
        }

        if (id == 2) {
            binding.vChat.setVisibility(View.GONE);
            binding.btnFav.setImageResource(R.drawable.icon_fav_onclick);
            isFavFragment = true;
        } else {
            binding.btnFav.setImageResource(R.drawable.icon_fav);
            isFavFragment = false;
        }

        if (id == 3) {
            binding.vChat.setVisibility(View.VISIBLE);
            binding.btnProfile.setImageResource(R.drawable.icon_me_onclick);
        } else {
            binding.btnProfile.setImageResource(R.drawable.icon_me);
        }
    }

    void getFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentId, fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(getBaseContext(), MyService.class);
        stopService(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.deleteNotificationChannel(CHANNEL_ID);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isPushNotifi) {
            getFragment(HomeFragmet.newInstance());
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFavFragment) {
            getFragment(FavoriteFragment.newInstance());
        }
    }
}