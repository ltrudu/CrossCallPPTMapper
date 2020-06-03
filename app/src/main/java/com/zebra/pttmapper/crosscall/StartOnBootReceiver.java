package com.zebra.pttmapper.crosscall;

/*
Original Source Code from James Swinton Bland:
        https://github.com/JamesSwinton/HyteraPTTMapper.git
        https://jamesswinton.com
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class StartOnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null &&
                intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Start Our Service
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, PTTMapperService.class));
            } else {
                context.startService(new Intent(context, PTTMapperService.class));
            }
        }
    }
}
