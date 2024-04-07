package com.datpv134.closeeyesandlisten.service;

import static com.datpv134.closeeyesandlisten.service.MyApplication.CHANNEL_ID;
import static com.datpv134.closeeyesandlisten.service.MyApplication.getCurrentPos;
import static com.datpv134.closeeyesandlisten.service.MyApplication.isPushNotifi;
import static com.datpv134.closeeyesandlisten.service.MyApplication.isRunning;
import static com.datpv134.closeeyesandlisten.service.MyApplication.mediaPlayer;
import static com.datpv134.closeeyesandlisten.service.MyApplication.repeatCode;
import static com.datpv134.closeeyesandlisten.service.MyApplication.songListInHome;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.Color;
import android.media.MediaPlayer;

import android.os.Bundle;
import android.os.IBinder;

import android.util.Log;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;

import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.model.Song;

import com.datpv134.closeeyesandlisten.ui.MusicPlayerActivity;

import java.io.IOException;

public class MyService extends Service {
    private static final int ACTION_PAUSE = 1;
    private static final int ACTION_RESUME = 2;
    private static final int ACTION_NEXT = 3;
    private static final int ACTION_BACK = 4;

    private Song song;
    Bitmap bitmap = null;
    boolean isPlaying, isNewSong;
    private boolean isOnlyService = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("My Service", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("myService", "onStartCommand");
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            Song temp = (Song) bundle.get("song1");
            isPlaying = bundle.getBoolean("isPlaying", false);
            isNewSong = bundle.getBoolean("isNewSong", false);
            if (isNewSong) {
                sendMessage(5);
            }
            if (temp != null) {
                song = ((MyApplication) this.getApplication()).getCurrentSong();

                sendNotificationMedia();
                sendNotificationMedia();
            }
        }

        int actionMusic = intent.getIntExtra("action_music_service", -1);
        handleActionMusic(actionMusic);

        return START_NOT_STICKY;
    }

    private void handleActionMusic(int action) {
        switch (action) {
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_RESUME:
                resumeMusic();
                break;
            case ACTION_NEXT:
                if (isRunning) {
                    sendMessage(3);
                    sendNotificationMedia();
                } else {
                    nextSongInForeground();
                    isPlaying = true;
                    sendNotificationMedia();
                }
                sendNotificationMedia();
                break;
            case ACTION_BACK:
                if (isRunning) {
                    sendMessage(4);
                    sendNotificationMedia();
                } else {
                    previousSongInForeground();
                    isPlaying = true;
                    sendNotificationMedia();
                }
                sendNotificationMedia();
                break;
            default:
                break;
        }
    }

    private void previousSongInForeground() {
        if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
            mediaPlayer.stop();
        }

        mediaPlayer.reset();
        mediaPlayer.release();

        mediaPlayer = new MediaPlayer();

        if (getCurrentPos > 0) {
            song = songListInHome.get(getCurrentPos - 1);
            getCurrentPos -= 1;

            prepareMediaPlayerInService();
        } else {
            song = songListInHome.get(songListInHome.size() - 1);
            getCurrentPos = songListInHome.size() - 1;

            prepareMediaPlayerInService();
        }

        setNewSong();

        setOnCompleteASongInService();
    }

    private void nextSongInForeground() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        mediaPlayer.reset();
        mediaPlayer.release();

        mediaPlayer = new MediaPlayer();

        if (getCurrentPos < (songListInHome.size() - 1)) {
            song = songListInHome.get(getCurrentPos + 1);
            getCurrentPos += 1;

            prepareMediaPlayerInService();
        } else {
            song = songListInHome.get(0);
            getCurrentPos = 0;

            prepareMediaPlayerInService();
        }

        setNewSong();

        setOnCompleteASongInService();
    }

    private void setNewSong() {
        ((MyApplication) this.getApplication()).setCurrentSong(song);
    }

    private void setOnCompleteASongInService() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();

                if (repeatCode == 2) {
                    repeatSongInService();
                    return;
                }

                if (getCurrentPos == (songListInHome.size() - 1)) {
                    if (repeatCode == 0) {
                        nextSongNotStartInService();
                        return;
                    } else if (repeatCode == 1) {
                        nextSongInForeground();
                        return;
                    }
                } else {
                    nextSongInForeground();
                    return;
                }

                sendNotificationMedia();
            }
        });
    }

    private void nextSongNotStartInService() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        mediaPlayer.reset();
        mediaPlayer.release();

        mediaPlayer = new MediaPlayer();


        song = songListInHome.get(0);
        getCurrentPos = 0;

        try {
            mediaPlayer.setDataSource(song.getSrc());
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    setNewSong();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendNotificationMedia();
        sendNotificationMedia();
    }

    private void repeatSongInService() {
        try {
            mediaPlayer.setDataSource(song.getSrc());
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            ((MyApplication) this.getApplication()).setCurrentSong(song);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Loi mang", Toast.LENGTH_SHORT).show();
        }
    }

    private void prepareMediaPlayerInService() {
        try {
            mediaPlayer.setDataSource(song.getSrc());
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();

                    sendNotificationMedia();
                    sendNotificationMedia();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Loi mang", Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseMusic() {
        if (isRunning) {
            sendMessage(1);
        }
        isPlaying = false;
        sendNotificationMedia();
    }

    private void resumeMusic() {
        if (isRunning) {
            sendMessage(2);
        }
        isPlaying = true;
        sendNotificationMedia();
    }

    private void sendMessage(int action) {
        Intent intent = new Intent("my_message");
        intent.putExtra("my_action", action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        isPushNotifi = false;
        super.onDestroy();
    }

    private void sendNotificationMedia() {
        setNewSong();
        try {
            bitmap = Glide.with(getBaseContext())
                    .asBitmap()
                    .load(song.getImage())
                    .submit(512, 512)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent;
        intent = new Intent(this, MusicPlayerActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//
        int color = Color.argb(255, 61, 220, 132);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                // Show controls on lock screen even when user hides sensitive content.
                .setContentTitle("Hello")
                .setContentText(song.getName())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.icon_music)
                .setOngoing(false)
                .setContentIntent(pendingIntent)
                // Add media control buttons that invoke intents in your media service
//                .addAction(R.drawable.icon_back, "Previous", getPendingIntent(this, ACTION_BACK)) // #0
//                .addAction(R.drawable.icon_pause, "Pause", getPendingIntent(this, ACTION_PAUSE))  // #1
//                .addAction(R.drawable.icon_next, "Next", getPendingIntent(this, ACTION_NEXT))     // #2
                // Apply the media style template
                .setLargeIcon(bitmap)
                .setColor(color)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2 /* #1: pause button */));

        if (isPlaying) {
            notification.addAction(R.drawable.icon_previous_song, "Previous", getPendingIntent(this, ACTION_BACK)) // #0
                    .addAction(R.drawable.icon_pause_2, "Pause", getPendingIntent(this, ACTION_PAUSE))  // #1
                    .addAction(R.drawable.icon_next, "Next", getPendingIntent(this, ACTION_NEXT));
        } else {
            notification.addAction(R.drawable.icon_previous_song, "Previous", getPendingIntent(this, ACTION_BACK)) // #0
                    .addAction(R.drawable.ic_play_2, "Pause", getPendingIntent(this, ACTION_RESUME))  // #1
                    .addAction(R.drawable.icon_next, "Next", getPendingIntent(this, ACTION_NEXT));
        }

        Notification notification1 = notification.build();

        startForeground(1, notification1);

        isPushNotifi = true;
    }

//    private void changeButton() {
//        if (isPlaying) {
//            remoteViews.setOnClickPendingIntent(R.id.imgPlayOrPauseNotifi, getPendingIntent(this, ACTION_PAUSE));
//            remoteViews.setImageViewResource(R.id.imgPlayOrPauseNotifi, R.drawable.icon_pause_2);
//        } else {
//            remoteViews.setOnClickPendingIntent(R.id.imgPlayOrPauseNotifi, getPendingIntent(this, ACTION_RESUME));
//            remoteViews.setImageViewResource(R.id.imgPlayOrPauseNotifi, R.drawable.ic_play_2);
//        }
//    }

    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra("action_music", action);

        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
