package com.datpv134.closeeyesandlisten.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import static com.datpv134.closeeyesandlisten.service.MyApplication.isCallInHomeReady;
import static com.datpv134.closeeyesandlisten.service.MyApplication.lofiChillList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.loveList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.remixList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.songList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.songListInHome;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.adapter.APIClientPM;
import com.datpv134.closeeyesandlisten.adapter.IOnClickSong;
import com.datpv134.closeeyesandlisten.adapter.SongAdapter;
import com.datpv134.closeeyesandlisten.databinding.FragmentHomeBinding;
import com.datpv134.closeeyesandlisten.model.MyNotification;
import com.datpv134.closeeyesandlisten.model.Song;
import com.datpv134.closeeyesandlisten.service.MyApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragmet extends Fragment {
    FragmentHomeBinding binding;
    private SongAdapter chillAdapter, remixAdapter, loveAdapter;
    private List<Song> filteredList = new ArrayList<Song>();
    private SongAdapter searchAdapter;

    public static HomeFragmet newInstance() {

        Bundle args = new Bundle();

        HomeFragmet fragment = new HomeFragmet();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        RecyclerView.LayoutManager managerChill = new GridLayoutManager(getContext(), 1, RecyclerView.HORIZONTAL, false);
        RecyclerView.LayoutManager managerLove = new GridLayoutManager(getContext(), 1, RecyclerView.HORIZONTAL, false);
        RecyclerView.LayoutManager managerRemix = new GridLayoutManager(getContext(), 1, RecyclerView.HORIZONTAL, false);

        Call<List<Song>> call = APIClientPM.getInstance().getSongs();

        call.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songList = response.body();
                songListInHome = songList;

                lofiChillList.clear();
                remixList.clear();
                loveList.clear();

                for (Song song : songList) {
                    if (Objects.equals(song.getPlayList(), "Lofi chill")) {
                        lofiChillList.add(song);
                    } else if (Objects.equals(song.getPlayList(), "Remix")) {
                        remixList.add(song);
                    } else if (Objects.equals(song.getPlayList(), "Love")) {
                        loveList.add(song);
                    }
                }

                isCallInHomeReady = true;
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {

            }
        });

        chillAdapter = new SongAdapter(lofiChillList, getContext(), new IOnClickSong() {
            @SuppressLint("UseRequireInsteadOfGet")
            @Override
            public void onClickSong(Song song) {
                Intent intent2 = new Intent(getContext(), MusicPlayerActivity.class);
                intent2.putExtra("Song", song);
                intent2.putExtra("SongList", (ArrayList<Song>) lofiChillList);
                startActivity(intent2);
            }
        });

        remixAdapter = new SongAdapter(remixList, getContext(), new IOnClickSong() {
            @Override
            public void onClickSong(Song song) {
                Intent intent2 = new Intent(getContext(), MusicPlayerActivity.class);
                intent2.putExtra("Song", song);
                intent2.putExtra("SongList", (ArrayList<Song>) remixList);
                startActivity(intent2);
            }
        });

        loveAdapter = new SongAdapter(loveList, getContext(), new IOnClickSong() {
            @Override
            public void onClickSong(Song song) {
                Intent intent2 = new Intent(getContext(), MusicPlayerActivity.class);
                intent2.putExtra("Song", song);
                intent2.putExtra("SongList", (ArrayList<Song>) loveList);
                startActivity(intent2);
            }
        });

        RecyclerView.LayoutManager managerSearch = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        searchAdapter = new SongAdapter(filteredList, getContext(), new IOnClickSong() {
            @Override
            public void onClickSong(Song song) {
                Intent intent2 = new Intent(getContext(), MusicPlayerActivity.class);
                intent2.putExtra("Song", song);
                intent2.putExtra("SongList", (ArrayList<Song>) songList);

                startActivity(intent2);
            }
        });

        binding.imgNotifiHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), MainActivity.class);
//                intent.putExtra("openNotifi", true);
//                startActivity(intent);
                startActivity(new Intent(getContext(), InUpdatingActivity.class));
            }
        });

        binding.rvSongSearch.setLayoutManager(managerSearch);
        binding.rvSongSearch.setAdapter(searchAdapter);

        binding.sbSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                filterSearchAgain();
            }
        });

        binding.sbSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor", "NotifyDataSetChanged"})
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterList(binding.sbSearch.getText().toString());
            }

            @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.rvChill.setLayoutManager(managerChill);
        binding.rvLove.setLayoutManager(managerLove);
        binding.rvRemix.setLayoutManager(managerRemix);

        binding.rvChill.setAdapter(chillAdapter);
        binding.rvLove.setAdapter(loveAdapter);
        binding.rvRemix.setAdapter(remixAdapter);

        binding.imgHistoryHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), InUpdatingActivity.class));
            }
        });

        return binding.getRoot();
    }

    private void filterSearchAgain() {
        if (binding.sbSearch.hasFocus()) {
            filterList(binding.sbSearch.getText().toString());
        } else {
            filterList(binding.sbSearch.getText().toString());
        }
    }

    @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    private void filterList(String s) {
        filteredList.clear();
        for (Song song : songList) {
            if (song.getName().toLowerCase().contains(s.toLowerCase())) {
                filteredList.add(song);
            }
        }

        searchAdapter.notifyDataSetChanged();

        if (s.length() < 1) {
            binding.layoutL1.setVisibility(View.VISIBLE);
            binding.layoutL2.setVisibility(View.VISIBLE);
            binding.layoutL3.setVisibility(View.VISIBLE);
            binding.tvSearchTitle.setVisibility(View.GONE);
            binding.rvSongSearch.setVisibility(View.GONE);
            binding.sbSearch.setBackground(getResources().getDrawable(R.drawable.search_bar));
            return;
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
            filteredList.clear();
            searchAdapter.notifyDataSetChanged();
        } else {
            binding.sbSearch.setBackground(getResources().getDrawable(R.drawable.search_bar_active));
            binding.layoutL1.setVisibility(View.GONE);
            binding.layoutL2.setVisibility(View.GONE);
            binding.layoutL3.setVisibility(View.GONE);
            binding.tvSearchTitle.setVisibility(View.VISIBLE);
            binding.rvSongSearch.setVisibility(View.VISIBLE);
            searchAdapter.notifyDataSetChanged();
        }
    }
}
