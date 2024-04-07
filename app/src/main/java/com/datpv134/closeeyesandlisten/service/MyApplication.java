package com.datpv134.closeeyesandlisten.service;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

import com.datpv134.closeeyesandlisten.model.MyFavoriteSong;
import com.datpv134.closeeyesandlisten.model.MyNotification;
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

public class MyApplication extends Application {
    public static final String CHANNEL_ID = "CHANNEL_ID";
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static Song song = new Song();
    public static boolean isCallInHomeReady = false;
    public static List<Song> songList = new ArrayList<Song>();
    public static List<Song> songListInHome = new ArrayList<Song>();
    public static List<Song> lofiChillList = new ArrayList<Song>();
    public static List<Song> loveList = new ArrayList<Song>();
    public static List<Song> remixList = new ArrayList<Song>();
    public static List<Song> soundTrackList = new ArrayList<Song>();
    public static List<MyFavoriteSong> favoriteList = new ArrayList<MyFavoriteSong>();
    private static List<MyNotification> myNotifications = new ArrayList<MyNotification>();
    public static int getCurrentPos = 0;
    public static boolean isRunning = false;
    public static Boolean isPushNotifi = false;
    public static boolean isShuffe = false;
    public static int repeatCode = 0;
    public static String userId;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String strFav;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    public List<MyNotification> getMyNotifications() {
        return myNotifications;
    }

    public void setMyNotifications(List<MyNotification> myNotifications) {
        MyApplication.myNotifications = myNotifications;
    }

    public Song getCurrentSong() {
        return song;
    }

    public void setCurrentSong(Song song) {
        this.song = song;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel", NotificationManager.IMPORTANCE_DEFAULT);

            channel.setSound(null, null);

            //getFavList();

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onTerminate() {
        Intent intent = new Intent(getBaseContext(), MyService.class);
        stopService(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.deleteNotificationChannel(CHANNEL_ID);
            }
        }
        super.onTerminate();
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
                                favoriteList.add(new MyFavoriteSong(s.getId(), s.getName(), s.getAuthor(), s.getPlayList(), s.getImage(), s.getSrc()));
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
