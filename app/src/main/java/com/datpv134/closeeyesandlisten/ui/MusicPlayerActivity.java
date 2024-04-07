package com.datpv134.closeeyesandlisten.ui;

import static com.datpv134.closeeyesandlisten.service.MyApplication.favoriteList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.lofiChillList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.loveList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.mediaPlayer;
import static com.datpv134.closeeyesandlisten.service.MyApplication.isRunning;
import static com.datpv134.closeeyesandlisten.service.MyApplication.getCurrentPos;
import static com.datpv134.closeeyesandlisten.service.MyApplication.isShuffe;
import static com.datpv134.closeeyesandlisten.service.MyApplication.remixList;
import static com.datpv134.closeeyesandlisten.service.MyApplication.repeatCode;
import static com.datpv134.closeeyesandlisten.service.MyApplication.songListInHome;
import static com.datpv134.closeeyesandlisten.service.MyApplication.userId;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.databinding.ActivityMusicPlayerBinding;
import com.datpv134.closeeyesandlisten.model.MyFavoriteSong;
import com.datpv134.closeeyesandlisten.model.Song;
import com.datpv134.closeeyesandlisten.service.MyApplication;
import com.datpv134.closeeyesandlisten.service.MyService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class MusicPlayerActivity extends AppCompatActivity {
    ActivityMusicPlayerBinding binding;
    private final Handler handler = new Handler();
    private Song song;
    int myAction;
    private Intent intent1;
    private Bundle bundleF = new Bundle();
    private int isFirstTime = 0;
    boolean isFav = false;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String strFav;

    private void setUpFirebase() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("Users");
            userId = user.getUid();

            myRef.child(userId).child("favoriteSong").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    strFav = snapshot.getValue(String.class);
                    if (strFav != null && !strFav.equals("")) {
                        if (strFav.contains(";" + song.getId() + ";")) {
                            isFav = true;
                            binding.iconFav.setImageResource(R.drawable.ic_baseline_favorite_24);
                        } else {
                            isFav = false;
                            binding.iconFav.setImageResource(R.drawable.icon_fav);
                        }
                    }
                    Log.d("firebase", "Value is: " + strFav);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(messageReceiver, new IntentFilter("my_message"));
        isRunning = true;

        setUpView();
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            myAction = intent.getIntExtra("my_action", -1);
            if (myAction == 1) {
                playOrPause();
            } else if (myAction == 2) {
                playOrPause();
            } else if (myAction == 3) {
                nextSong();
            } else if (myAction == 4) {
                previousSong();
            } else if (myAction == 5) {
                stopService(intent1);
                bundleF.putSerializable("song1", song);
                bundleF.putBoolean("isPlaying", false);
                bundleF.putBoolean("isNewSong", false);
                intent1.putExtras(bundleF);
                startService(intent1);
            } else if (myAction == 0) {
                stopService(intent1);
            }
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        isRunning = false;
        super.onPause();
        onStop();
    }

    private void startNewSong() {
        mediaPlayer.stop();
        mediaPlayer.reset();

        mediaPlayer = new MediaPlayer();

        bundleF.putSerializable("song1", song);
        bundleF.putBoolean("isPlaying", false);
        bundleF.putBoolean("isNewSong", true);
        intent1.putExtras(bundleF);
        startService(intent1);

        prepareMediaPlayer();

        ((MyApplication) this.getApplication()).setCurrentSong(song);
    }


    private void startOldSong() {
        binding.sbPlayer.setMax(100);
        setButtonPlayOrPause();
        binding.sbPlayer.setSecondaryProgress(100);
        continueOldSong();
    }

    private void setNewSong() {
        ((MyApplication) this.getApplication()).setCurrentSong(song);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_music_player);

        setUpFirebase();

        Song s = ((MyApplication) this.getApplication()).getCurrentSong();

        //       boolean checkComeBack = getIntent().getBooleanExtra("comeback", false);

//        if () {
        songListInHome = (ArrayList<Song>) getIntent().getSerializableExtra("SongList");
        song = (Song) getIntent().getSerializableExtra("Song");

        if (songListInHome == null || song == null) {
            song = s;
            if (song.getPlayList().equals("Lofi chill")) {
                songListInHome = lofiChillList;
            }
            if (song.getPlayList().equals("Love")) {
                songListInHome = loveList;
            }
            if (song.getPlayList().equals("Remix")) {
                songListInHome = remixList;
            }
        }

        intent1 = new Intent(getBaseContext(), MyService.class);
        bundleF = new Bundle();


//        Log.e("size", songListInHome.size() + "");
//
//        } else {
//            song = s;
//        }


//        getCurrentPos = songListInHome.indexOf(song);
//        if (Objects.equals(song.getId(), "-1")) {
//            mediaPlayer.stop();
//            isRunning = false;
//            isPushNotifi = false;
//            startActivity(new Intent(getBaseContext(), MainActivity.class));
//        }

//        bundleF.putSerializable("song1", song);
//        bundleF.putBoolean("isPlaying", false);
//        bundleF.putBoolean("isNewSong", false);
//        intent1.putExtras(bundleF);
//        startService(intent1);
        for (int i = 0; i < songListInHome.size(); i++) {
            if (song.getId().equals(songListInHome.get(i).getId())) {
                getCurrentPos = i;
                break;
            }
        }

        binding.sbPlayer.setMax(100);

        if (!mediaPlayer.isPlaying()) {
            binding.sbPlayer.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
        }

        Log.e("Song", song.getId());

        if (!Objects.equals(song.getId(), s.getId()) || s.getId() == "-1") {
            setUpView();
            startNewSong();
            Log.e("new song", "new song");
        } else {
            Log.e("old song", "old song");
            setUpView();
            startOldSong();
        }

        setOtherButtonBeginState();

//
//        Bundle bundleF = new Bundle();
//        bundleF.putSerializable("song1", song);
//        bundleF.putBoolean("isPlaying", true);
//        intent1.putExtras(bundleF);
//
//        startService(intent1);


//        mediaPlayer = new MediaPlayer();

        binding.imagePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playOrPause();
            }
        });

//        prepareMediaPlayer();

        binding.sbPlayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                SeekBar seekBar = (SeekBar) view;
                int playPosition = (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
                mediaPlayer.seekTo(playPosition);
                binding.tvCurrentTime.setText(miliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                return false;
            }
        });


        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                binding.sbPlayer.setSecondaryProgress(i);
            }
        });

        isRunning = true;

        setOnCompleteASong();

        onClickOtherButton();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setUpView();
    }

    private void setOnCompleteASong() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                binding.sbPlayer.setProgress(0);
                binding.imagePlay.setImageResource(R.drawable.icon_play);
                binding.tvCurrentTime.setText(R.string.zero);
                mediaPlayer.reset();

                if (isFirstTime == 0) {
                    prepareMediaPlayer();
                    isFirstTime++;
                    return;
                }

                if (repeatCode == 2) {
                    repeatSong();
                    return;
                }

                if (getCurrentPos == (songListInHome.size() - 1)) {
                    if (repeatCode == 0) {
                        nextSongNotStart();
                        return;
                    } else if (repeatCode == 1) {
                        nextSong();
                        return;
                    }
                } else {
                    nextSong();
                    return;
                }

//              binding.tvTotalTime.setText(miliSecondsToTimer(mediaPlayer.getDuration()));
            }
        });
    }

    private void setUpView() {
        Glide.with(getBaseContext())
                .load(song.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgSongPlayer);

        binding.tvSongName.setText(song.getName());
        binding.tvSongAuthor.setText(song.getAuthor());
    }

    private void nextSongNotStart() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        mediaPlayer.reset();
        mediaPlayer.release();

        mediaPlayer = new MediaPlayer();


        song = songListInHome.get(0);
        getCurrentPos = 0;

        Glide.with(getBaseContext())
                .load(song.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgSongPlayer);

        binding.tvSongName.setText(song.getName());
        binding.tvSongAuthor.setText(song.getAuthor());

        try {
            mediaPlayer.setDataSource(song.getSrc());
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    binding.tvTotalTime.setText(miliSecondsToTimer(mediaPlayer.getDuration()));
                    setNewSong();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.sbPlayer.setSecondaryProgress(100);

        bundleF.putSerializable("song1", song);
        bundleF.putBoolean("isPlaying", false);
        bundleF.putBoolean("isNewSong", false);
        intent1.putExtras(bundleF);
        startService(intent1);
    }


    private void repeatSong() {
        try {
            mediaPlayer.setDataSource(song.getSrc());
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    binding.imagePlay.setImageResource(R.drawable.icon_pause);

                    bundleF.putSerializable("song1", song);
                    bundleF.putBoolean("isPlaying", true);
                    bundleF.putBoolean("isNewSong", false);
                    intent1.putExtras(bundleF);
                    startService(intent1);

                    updateSeekBar();
                }
            });
            binding.tvTotalTime.setText(miliSecondsToTimer(mediaPlayer.getDuration()));
            ((MyApplication) this.getApplication()).setCurrentSong(song);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Loi mang", Toast.LENGTH_SHORT).show();
        }
    }

    private void setOtherButtonBeginState() {
        if (isShuffe) {
            binding.imgShuffing.setImageResource(R.drawable.ic_shuffed);
        } else {
            binding.imgShuffing.setImageResource(R.drawable.icon_shuffing);
        }

        if (repeatCode == 0) {
            binding.imgRepeat.setImageResource(R.drawable.icon_repeat);
        } else if (repeatCode == 1) {
            binding.imgRepeat.setImageResource(R.drawable.ic_repeat_list);
        } else if (repeatCode == 2) {
            repeatCode = 0;
            binding.imgRepeat.setImageResource(R.drawable.ic_repeat_one);
        }
    }

    private void onClickOtherButton() {
        AlertDialog alertDialog = new AlertDialog.Builder(MusicPlayerActivity.this)
                .setTitle("Thông báo")
                .setMessage("Bạn cần đăng nhập để có thể sử dụng chức năng này")
                .setPositiveButton("Tắt thông báo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        binding.iconFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    alertDialog.show();
                    return;
                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                MyFavoriteSong favoriteSong = new MyFavoriteSong(song.getId(), song.getName(), song.getAuthor(), song.getPlayList(), song.getImage(), song.getSrc());
                if (isFav) {
                    String temp = strFav;
                    temp = strFav.replace(";" + song.getId() + ";", ";");
                    strFav = temp;
                    database.getReference().child("Users").child(userId).child("favoriteSong").setValue(strFav);
                    binding.iconFav.setImageResource(R.drawable.icon_fav);
                    favoriteList.remove(favoriteSong);
                    isFav = false;
                } else {
                    strFav += song.getId() + ";";
                    database.getReference().child("Users").child(userId).child("favoriteSong").setValue(strFav);
                    binding.iconFav.setImageResource(R.drawable.ic_baseline_favorite_24);
                    favoriteList.add(favoriteSong);
                    isFav = true;
                }
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousSong();
            }
        });

        binding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextSong();
            }
        });

        binding.imgShuffing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShuffe) {
                    Collections.sort(songListInHome, new Comparator<Song>() {
                        @Override
                        public int compare(Song song, Song t1) {
                            return Integer.parseInt(song.getId()) - Integer.parseInt(t1.getId());
                        }
                    });
                    isShuffe = false;
                    binding.imgShuffing.setImageResource(R.drawable.icon_shuffing);
                } else {
                    Collections.shuffle(songListInHome);
                    isShuffe = true;
                    binding.imgShuffing.setImageResource(R.drawable.ic_shuffed);
                }
            }
        });

        binding.imgRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatCode == 0) {
                    repeatCode = 1;
                    binding.imgRepeat.setImageResource(R.drawable.ic_repeat_list);
                } else if (repeatCode == 1) {
                    repeatCode = 2;
                    binding.imgRepeat.setImageResource(R.drawable.ic_repeat_one);
                } else if (repeatCode == 2) {
                    repeatCode = 0;
                    binding.imgRepeat.setImageResource(R.drawable.icon_repeat);
                }
            }
        });
    }

//    private int getPlayRepeat(int repeatCode) {
//        int playRepeat = 0;
//
//        switch (repeatCode) {
//            case 0: break;
//            case 1: break;
//            case 2: break;
//            default: break;
//        }
//
//        return ;
//    }


    private void nextSong() {
        bundleF.putSerializable("song1", song);
        bundleF.putBoolean("isPlaying", false);
        bundleF.putBoolean("isNewSong", false);
        intent1.putExtras(bundleF);
        startService(intent1);

        if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
            mediaPlayer.stop();
        }

        mediaPlayer.reset();
        mediaPlayer.release();

        mediaPlayer = new MediaPlayer();

        if (getCurrentPos < (songListInHome.size() - 1)) {
            song = songListInHome.get(getCurrentPos + 1);
            getCurrentPos += 1;
            Glide.with(getBaseContext())
                    .load(song.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imgSongPlayer);

            binding.tvSongName.setText(song.getName());
            binding.tvSongAuthor.setText(song.getAuthor());

            prepareMediaPlayer();
        } else {
            song = songListInHome.get(0);
            getCurrentPos = 0;
            Glide.with(getBaseContext())
                    .load(song.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imgSongPlayer);

            binding.tvSongName.setText(song.getName());
            binding.tvSongAuthor.setText(song.getAuthor());

            bundleF.putSerializable("song1", song);
            bundleF.putBoolean("isPlaying", true);
            bundleF.putBoolean("isNewSong", false);
            intent1.putExtras(bundleF);
            startService(intent1);

            prepareMediaPlayer();
        }

        setNewSong();

        binding.sbPlayer.setSecondaryProgress(100);

        setOnCompleteASong();
    }

    private void previousSong() {
        bundleF.putSerializable("song1", song);
        bundleF.putBoolean("isPlaying", false);
        bundleF.putBoolean("isNewSong", false);
        intent1.putExtras(bundleF);
        startService(intent1);

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
            Glide.with(getBaseContext())
                    .load(song.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imgSongPlayer);

            binding.tvSongName.setText(song.getName());
            binding.tvSongAuthor.setText(song.getAuthor());

            prepareMediaPlayer();
        } else {
            song = songListInHome.get(songListInHome.size() - 1);
            getCurrentPos = songListInHome.size() - 1;
            Glide.with(getBaseContext())
                    .load(song.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imgSongPlayer);

            binding.tvSongName.setText(song.getName());
            binding.tvSongAuthor.setText(song.getAuthor());

            bundleF.putSerializable("song1", song);
            bundleF.putBoolean("isPlaying", true);
            bundleF.putBoolean("isNewSong", false);
            intent1.putExtras(bundleF);
            startService(intent1);

            prepareMediaPlayer();
        }

        setNewSong();

        binding.sbPlayer.setSecondaryProgress(100);

        setOnCompleteASong();
    }

    private void prepareMediaPlayer() {
        try {
            mediaPlayer.setDataSource(song.getSrc());
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    binding.imagePlay.setImageResource(R.drawable.icon_pause);

                    bundleF.putSerializable("song1", song);
                    bundleF.putBoolean("isPlaying", true);
                    bundleF.putBoolean("isNewSong", false);
                    intent1.putExtras(bundleF);
                    startService(intent1);

                    updateSeekBar();
                }
            });
            binding.tvTotalTime.setText(miliSecondsToTimer(mediaPlayer.getDuration()));
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Loi mang", Toast.LENGTH_SHORT).show();
        }
    }

    private void continueOldSong() {
        binding.tvCurrentTime.setText(miliSecondsToTimer(mediaPlayer.getCurrentPosition()));
        binding.tvTotalTime.setText(miliSecondsToTimer(mediaPlayer.getDuration()));
        updateSeekBar();
    }

    private final Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            binding.tvCurrentTime.setText(miliSecondsToTimer(currentDuration));
        }
    };

    private void updateSeekBar() {
        if (mediaPlayer.isPlaying()) {
            binding.sbPlayer.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }
    }

    private void playOrPause() {
        if (mediaPlayer.isPlaying()) {
            handler.removeCallbacks(updater);
            mediaPlayer.pause();
            binding.imagePlay.setImageResource(R.drawable.icon_play);
            bundleF.putSerializable("song1", song);
            bundleF.putBoolean("isPlaying", false);
            bundleF.putBoolean("isNewSong", false);
            intent1.putExtras(bundleF);
            startService(intent1);
        } else {
            mediaPlayer.start();
            binding.imagePlay.setImageResource(R.drawable.icon_pause);
            updateSeekBar();
            bundleF.putSerializable("song1", song);
            bundleF.putBoolean("isPlaying", true);
            bundleF.putBoolean("isNewSong", false);
            intent1.putExtras(bundleF);
            startService(intent1);
        }
    }

    private void setButtonPlayOrPause() {
        if (mediaPlayer.isPlaying()) {
            binding.imagePlay.setImageResource(R.drawable.icon_pause);
        } else {
            binding.imagePlay.setImageResource(R.drawable.icon_play);
        }
    }

    private String miliSecondsToTimer(long miliSeconds) {
        String timerString = "";
        String secondString;

        int hours = (int) (miliSeconds / (1000 * 60 * 60));
        int minutes = (int) (miliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((miliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            timerString = hours + ":";
        }

        if (seconds < 10) {
            secondString = "0" + seconds;
        } else {
            secondString = "" + seconds;
        }
        timerString = timerString + minutes + ":" + secondString;

        return timerString;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;
        setUpView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
    }
}