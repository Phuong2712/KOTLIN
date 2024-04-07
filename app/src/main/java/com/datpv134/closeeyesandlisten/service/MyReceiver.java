package com.datpv134.closeeyesandlisten.service;

import static com.datpv134.closeeyesandlisten.service.MyApplication.isRunning;
import static com.datpv134.closeeyesandlisten.service.MyApplication.mediaPlayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int actionMusic = intent.getIntExtra("action_music", 0);

        Intent intentService;

        if (isRunning || actionMusic == 5) {
            intentService = new Intent(context, MyService.class);
            intentService.putExtra("action_music_service", actionMusic);

            context.startService(intentService);
        } else {
            if (actionMusic == 1) {
                mediaPlayer.pause();
                intentService = new Intent(context, MyService.class);
                intentService.putExtra("action_music_service", 1);

                context.startService(intentService);
            } else if (actionMusic == 2) {
                mediaPlayer.start();
                intentService = new Intent(context, MyService.class);
                intentService.putExtra("action_music_service", 2);

                context.startService(intentService);
            } else if (actionMusic == 3) {
                intentService = new Intent(context, MyService.class);
                intentService.putExtra("action_music_service", 3);

                context.startService(intentService);
            } else if (actionMusic == 4) {
                intentService = new Intent(context, MyService.class);
                intentService.putExtra("action_music_service", 4);

                context.startService(intentService);
            }
        }
    }
}
