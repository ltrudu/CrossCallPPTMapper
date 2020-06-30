package com.zebra.pttmapper.crosscall;

/*
Original Source Code from James Swinton Bland:
        https://github.com/JamesSwinton/HyteraPTTMapper.git
        https://jamesswinton.com
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class PTTMapperService extends Service {

    // Debugging
    private static final String TAG = "PTTMapperService";

    // Service Notification
    private static final int FOREGROUND_NOTIFICATION_ID = 1;
    private static final String ACTION_STOP_SERVICE = "action_stop_service";

    // PTT Intents
    private static final String PTT_PRESSED = "com.symbol.wfc.ptt_pressed";
    private static final String PTT_RELEASED = "com.symbol.wfc.ptt_released";

    // Hytera Intents
    private static final int PTT_KEYCODE = 142;


    private static final String KEYEVENT_ACTION_TO_LISTEN_PTT2_PRESSED =
            "android.intent.action.FUNCTION_KEY_DOWN_PRESSED";

    private static final String KEYEVENT_ACTION_TO_LISTEN_PTT2_RELEASED =
            "android.intent.action.FUNCTION_KEY_RELEASED";



    @Override
    public void onCreate() {
        super.onCreate();

        // Register Button Receiver
        IntentFilter hwbuttonIntentFilter = new IntentFilter();
        hwbuttonIntentFilter.addAction(KEYEVENT_ACTION_TO_LISTEN_PTT2_RELEASED);
        hwbuttonIntentFilter.addAction(KEYEVENT_ACTION_TO_LISTEN_PTT2_PRESSED);
        registerReceiver(customButtonReceiver, hwbuttonIntentFilter);

        // Start Service
        startForeground(FOREGROUND_NOTIFICATION_ID, createServiceNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service Started");
        if (intent.getAction() != null && intent.getAction().equals(ACTION_STOP_SERVICE)) {
            stopSelf();
            return START_NOT_STICKY;
        } else {
            return START_STICKY;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(customButtonReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private Notification createServiceNotification() {
        // Create Variables
        String channelId = "com.zebra.pttmapper.crosscall";
        String channelName = "Crosscall PTT Mapper Service Interface Channel";

        // Create Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_NONE);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Set Channel
            NotificationManager manager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }

        // Build Notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                channelId);

        // Build StopService action
        Intent stopIntent = new Intent(this, PTTMapperService.class);
        stopIntent.setAction(ACTION_STOP_SERVICE);
        PendingIntent stopPendingIntent = PendingIntent.getService(this,
                0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action stopServiceAction = new NotificationCompat.Action(
                R.drawable.ic_stop, "Stop Remapping", stopPendingIntent
        );

        // Return Build Notification object
        return notificationBuilder
                .setContentTitle("PTT Remapper Active")
                .setSmallIcon(R.drawable.ic_remap)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOngoing(true)
                .addAction(stopServiceAction)
                .build();
    }

    private BroadcastReceiver customButtonReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && (action.equals(KEYEVENT_ACTION_TO_LISTEN_PTT2_PRESSED))) {
                sendBroadcast(new Intent(PTT_PRESSED));
            }
            else if(action != null && action.equals(KEYEVENT_ACTION_TO_LISTEN_PTT2_RELEASED))
            {
                sendBroadcast(new Intent(PTT_RELEASED));
            }
        }
    };
}
