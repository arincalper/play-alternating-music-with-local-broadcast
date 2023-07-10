package com.example.eee335_lab4_alper_arinc;


import androidx.appcompat.app.AppCompatActivity;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    LocalService mService;
    boolean mStarted = false;
    boolean mBound = false;

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MAIN", "onCreate(), mBound: " + mBound + ", mStarted: " + mStarted);
        setContentView(R.layout.activity_main);
        setClickHandlerForStartService();
        setClickHandlerForStopService();
        setTitle("EEE335_Lab4_Alper_Arinc");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            TextView names = (TextView) findViewById(R.id.textView2);
            names.setText(message);
        }
    };

    //----------------------------------------------------------------------------------------------
    private void setClickHandlerForStartService() {
        Log.d("MAIN", "setClickHandlerForStartService(), mBound: " + mBound + ", mStarted: " + mStarted);
        Button btnStart = (Button) findViewById(R.id.btn_play);
        btnStart.setOnClickListener(v -> {
            Log.d("MAIN", "btnStart handler run(), mBound: " + mBound + ", mStarted: " + mStarted);
            Intent intent = new Intent(getApplicationContext(), LocalService.class);
            if (!mStarted) {
                startService(intent);
            }
            // Bind to LocalService
            if (!mBound) {
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
            }
        });
    }
    //----------------------------------------------------------------------------------------------
    private void setClickHandlerForStopService() {
        Log.d("MAIN", "setClickHandlerForStopService(), mBound: " + mBound + ", mStarted: " + mStarted);
        Button btnStop = (Button) findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("MAIN", "btnStop onClick() entry, mBound: " + mBound + ", mStarted: " + mStarted);
                if (mStarted) {
                    stopService(new Intent(getApplicationContext(), LocalService.class));
                    mStarted = false;
                }
                if (mBound) {
                    unbindService(connection);
                    mBound = false;
                }
                Log.d("MAIN", "btnStop onClick() exit, mBound: " + mBound + ", mStarted: " + mStarted);
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MAIN", "onStop() entry, mBound: " + mBound + ", mStarted: " + mStarted);
        //        if (mBound) {
        //            unbindService(connection);
        //            mBound = false;
        //        }
        //        Log.d("MAIN", "onStop() exit, mBound: " + mBound + ", mStarted: " + mStarted);
    }
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MAIN", "onDestroy() entry, mBound: " + mBound + ", mStarted: " + mStarted);
        if (mStarted) {
            stopService(new Intent(getApplicationContext(), LocalService.class));
            mStarted = false;
        }
        if (mBound) {
            unbindService(connection);
            mBound = false;
        }
        Log.d("MAIN", "onDestroy() exit, mBound: " + mBound + ", mStarted: " + mStarted);
    }
    //----------------------------------------------------------------------------------------------
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.d("MAIN", "onServiceConnected() entry, mBound: " + mBound + ", mStarted: " + mStarted);
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            mService = binder.getService();
            mStarted = true;
            mBound = true;
            Log.d("MAIN", "onServiceConnected() exit, mBound: " + mBound + ", mStarted: " + mStarted);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("MAIN", "onServiceDisconnected() entry, mBound: " + mBound + ", mStarted: " + mStarted);
            mStarted = false;
            mBound = false;
            Log.d("MAIN", "onServiceDisconnected() exit, mBound: " + mBound + ", mStarted: " + mStarted);
        }
    };
}