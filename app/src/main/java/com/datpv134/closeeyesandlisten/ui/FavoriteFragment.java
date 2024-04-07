package com.datpv134.closeeyesandlisten.ui;

import static com.datpv134.closeeyesandlisten.service.MyApplication.favoriteList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.songList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.userId;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.datpv134.closeeyesandlisten.adapter.IOnClickFavoriteSong;
import com.datpv134.closeeyesandlisten.adapter.MyFavoriteAdapter;
import com.datpv134.closeeyesandlisten.databinding.FragmentFavoriteBinding;
import com.datpv134.closeeyesandlisten.model.MyFavoriteSong;
import com.datpv134.closeeyesandlisten.model.Song;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavoriteFragment extends Fragment {
    FragmentFavoriteBinding binding;
    MyFavoriteAdapter adapter;
    private List<MyFavoriteSong> favSongs;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String strFav;

    public static FavoriteFragment newInstance() {
        
        Bundle args = new Bundle();
        
        FavoriteFragment fragment = new FavoriteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            binding.tvNeedLogin.setVisibility(View.VISIBLE);
            binding.tvFavTitle.setVisibility(View.GONE);
            binding.rvFav.setVisibility(View.GONE);
        }

        favSongs = new ArrayList<>();
        getFavList();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false);
        adapter = new MyFavoriteAdapter(favSongs, getContext(), new IOnClickFavoriteSong() {
            @Override
            public void onClickFavSong(MyFavoriteSong favoriteSong) {
                Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
                for (Song song : songList) {
                    if (Objects.equals(song.getId(), favoriteSong.getId())) {
                        favoriteList = favSongs;
                        List<Song> songs = new ArrayList<>();
                        for (MyFavoriteSong song1 : favoriteList) {
                            songs.add(new Song(song1.getId(), song1.getName(), song1.getAuthor(), song1.getPlayList(), song1.getImage(), song1.getSrc()));
                        }
                        intent.putExtra("Song", song);
                        intent.putExtra("SongList", (ArrayList<Song>) songs);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onClickFavIcon(MyFavoriteSong favoriteSong) {
                favSongs.remove(favoriteSong);
                favoriteList = favSongs;
                setFavList(favoriteSong);
                adapter.notifyDataSetChanged();
            }
        });

        binding.rvFav.setLayoutManager(layoutManager);
        binding.rvFav.setAdapter(adapter);

        return binding.getRoot();
    }

    private void setFavList(MyFavoriteSong favoriteSong) {
        strFav = strFav.replace(";" + favoriteSong.getId() + ";", ";");
        userId = user.getUid();
        database.getReference().child("Users").child(userId).child("favoriteSong").setValue(strFav);
    }

    private void getFavList() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("Users");
            userId = user.getUid();

            myRef.child(userId).child("favoriteSong").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    strFav = dataSnapshot.getValue(String.class);
                    if (strFav != null && !strFav.equals("")) {
                        for (Song s : songList) {
                            if (strFav.contains(";" + s.getId() + ";")) {
                                favSongs.add(new MyFavoriteSong(s.getId(), s.getName(), s.getAuthor(), s.getPlayList(), s.getImage(), s.getSrc()));
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                    Log.d("firebase", "Value is: " + strFav);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("firebase", "Failed to read value.", error.toException());
                }
            });
        }
    }
}
