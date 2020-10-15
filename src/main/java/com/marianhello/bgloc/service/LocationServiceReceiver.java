package com.marianhello.bgloc.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.PowerManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationServiceReceiver extends BroadcastReceiver {
  private static final int ALARM_FREQUENCY = 60*1000;
  private static final String TAG = "LOC_SERVICE_ALARM_RECEIVER";

  @Override
  public void onReceive(Context context, Intent intent) {
      if (intent.getAction() == "sg.gov.homer.location") {
        Log.d(TAG, "Alarm successfully fired");
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "sg.gov.homer.alarm");
        if(!wakeLock.isHeld()) {
            wakeLock.acquire();
        }

        // Schedule for next alarm
        scheduleExactAlarm(context, (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent customEvent = new Intent("my-custom-event");
        localBroadcastManager.sendBroadcast(customEvent);

        if (wakeLock.isHeld()) {
          wakeLock.release();
        }
      }
  }

  public static void scheduleExactAlarm(Context context, AlarmManager alarmMgr) {
    Intent intent = new Intent(context, LocationServiceReceiver.class);
    intent.setAction("sg.gov.homer.location");
    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
   
    // TODO: remove when done debugging
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS");
    Date resultdate = new Date((System.currentTimeMillis()+ALARM_FREQUENCY));
    String dateToShow = sdf.format(resultdate);
    Log.d(TAG, "scheduling next alarm at " + dateToShow);

    alarmMgr.setAlarmClock(new AlarmManager.AlarmClockInfo(System.currentTimeMillis()+ALARM_FREQUENCY, null), alarmIntent);
  }
}