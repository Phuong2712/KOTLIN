package com.datpv134.closeeyesandlisten.ui;

import static com.datpv134.closeeyesandlisten.service.MyApplication.isPushNotifi;
import static com.datpv134.closeeyesandlisten.service.MyApplication.songList;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.adapter.IOnClickNotification;
import com.datpv134.closeeyesandlisten.adapter.MyNotificationAdapter;
import com.datpv134.closeeyesandlisten.databinding.FragmentNotificationBinding;
import com.datpv134.closeeyesandlisten.model.MyNotification;
import com.datpv134.closeeyesandlisten.model.Song;
import com.datpv134.closeeyesandlisten.service.MyApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {
    FragmentNotificationBinding binding;
    private List<MyNotification> notifications;
    private MyNotificationAdapter adapter;

    public static NotificationFragment newInstance() {

        Bundle args = new Bundle();

        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);

        notifications = new ArrayList<MyNotification>();
        notifications = ((MyApplication) getActivity().getApplication()).getMyNotifications();

        if (notifications.size() != 0) {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false);
            adapter = new MyNotificationAdapter((ArrayList<MyNotification>) notifications, getContext(), new IOnClickNotification() {
                @Override
                public void onClickNotification(MyNotification mNotification) {
                    Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
                    for (Song song : songList) {
                        if (Objects.equals(song.getId(), mNotification.getId())) {
                            intent.putExtra("Song", song);
                            intent.putExtra("SongList", (ArrayList<Song>) songList);
                            startActivity(intent);
                        }
                    }
                }
            });

            binding.rvNotifi.setLayoutManager(layoutManager);
            binding.rvNotifi.setAdapter(adapter);
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        isPushNotifi = false;
        super.onDestroy();
    }

    @Override
    public void onStop() {
        isPushNotifi = false;
        super.onStop();
    }
}
