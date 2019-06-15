package com.stepprototype.getbatterylevel;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyService extends Service {

    private Notification mNotificationBuilder;


    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final IntentFilter battChangeFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        //register receiver
        this.registerReceiver(this.batteryChangeReceiver, battChangeFilter);
        keepServiceAlive();

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        return START_NOT_STICKY;
    }

    private final BroadcastReceiver batteryChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context,final Intent intent) {
            checkBatteryLevel(intent);
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void checkBatteryLevel(Intent batteryChangeIntent) {
        final int currentLevel = batteryChangeIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        final int maxLevel = batteryChangeIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        final int percentage = (int) Math.round((currentLevel * 100.0) / maxLevel);

        Log.d("MyService", "current battery level: " + percentage );

        writeData(percentage);

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        unregisterReceiver(batteryChangeReceiver);
        writeData(0);
        super.onDestroy();
    }

    private void writeData(int percentage) {
        UserBatteryInfo user = new UserBatteryInfo(String.valueOf(percentage), Build.MANUFACTURER + " " +Build.PRODUCT);

        MainActivity.mDatabase.child("UserDeviceBattery").child(Build.MANUFACTURER + " " +Build.PRODUCT).setValue(user);
    }

    private void keepServiceAlive() {

        mNotificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(isMyServiceRunning(MyService.class))
                .build();

        startForeground(Notification.FLAG_ONGOING_EVENT, mNotificationBuilder);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
