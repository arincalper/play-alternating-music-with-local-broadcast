package com.example.eee335_lab4_alper_arinc;



import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LocalService extends Service {
    private MediaPlayer player1, player2, player3;
    private NotificationManager mNM;
    private int NOTIFICATION = 0;
    private final IBinder mBinder = new LocalBinder();
    private static final String NOTIFICATION_ID_STRING = "My Notifications";

    //----------------------------------------------------------------------------------------------
    public class LocalBinder extends Binder {
        LocalService getService() {
            return LocalService.this;
        }
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LocalService", "onStartCommand()");
        player1 = MediaPlayer.create(this, R.raw.one_piece_funny_moment);
        player2 = MediaPlayer.create(this, R.raw.howling_furies_guitar_solo_part);
        player3 = MediaPlayer.create(this, R.raw.sound_effect3);

        Toast.makeText(this, "music starting...", Toast.LENGTH_SHORT).show();
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();

        player1.start();
        sendMessage();
        player1.setOnCompletionListener(mp -> {
            player2.start();
            sendMessage();
            player2.setOnCompletionListener(mp1 -> {
                player3.start();
                sendMessage();
                player3.setOnCompletionListener(mp2 -> {
                    sendMessage();
                });
            });
        });
        return START_STICKY;
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onCreate() {
        Log.d("LocalService", "onCreate()");

    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {
        Log.d("LocalService", "onDestroy()");
        if(player1.isPlaying()) {
            player1.stop();
            mNM.cancel(NOTIFICATION);
            Toast.makeText(this, "music stopping...", Toast.LENGTH_SHORT).show();
        }
        else if(player2.isPlaying()) {
            player2.stop();
            mNM.cancel(NOTIFICATION);
            Toast.makeText(this, "music stopping...", Toast.LENGTH_SHORT).show();
        }
        else {
            player3.stop();
            mNM.cancel(NOTIFICATION);
            Toast.makeText(this, "music stopping...", Toast.LENGTH_SHORT).show();
        }

    }

    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("custom-event-name");

        if(player1.isPlaying()){
            intent.putExtra("message", "1. one_piece_funny_moment");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        else if (player2.isPlaying()){
            intent.putExtra("message", "2. howling_furies_guitar_solo_part");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        else if (player3.isPlaying()){
            intent.putExtra("message", "3. sound_effect3");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        else {
            intent.putExtra("message", "no more songs!");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            mNM.cancel(NOTIFICATION);
        }
    }
    //----------------------------------------------------------------------------------------------
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    //----------------------------------------------------------------------------------------------
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "Tap to return to the application!";

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_ONE_SHOT);
        }
        else
        {
            contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_ONE_SHOT);
        }

        //Create the channel. Android will automatically check if the channel already exists
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID_STRING, "My Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My notification channel description");
            mNM.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notifyBuilder
                = new NotificationCompat.Builder(this, NOTIFICATION_ID_STRING)
                .setContentTitle("EEE335_Lab4_Alper_Arinc")
                .setContentText(text)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher_background);
        Notification myNotification = notifyBuilder.build();
        mNM.notify(NOTIFICATION, myNotification);
    }
}