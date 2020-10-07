package com.marianhello.bgloc.messaging;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.facebook.react.HeadlessJsTaskService;

import com.marianhello.bgloc.messaging.Utils;

public class LocationAlarmMessagingService {
  private static final String TAG = "LocationAlarmMessagingService";

  public static final String FOREGROUND_MESSAGE_EVENT = "foreground-location-alarm";

  // instance of our receiver to listen from foreground service timer
  private LocalTimedBroadcastReceiver mLocalBroadcastReceiver;
  private Context mContext;

  public LocationAlarmMessagingService(Context context) {
    Log.d(TAG, "Creating timed broadcast receiver");
    this.mLocalBroadcastReceiver = new LocalTimedBroadcastReceiver();
    this.mContext = context;
    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
    localBroadcastManager.registerReceiver(mLocalBroadcastReceiver, new IntentFilter("my-custom-event"));
  }

  public class LocalTimedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      Log.d(TAG, "Timed alarm broadcast received");

      // If the app is in the foreground we send it to the Messaging module
      if (Utils.isAppInForeground(mContext)) {
        Intent messagingEvent = new Intent(FOREGROUND_MESSAGE_EVENT);
        messagingEvent.putExtra("my-extra-data", "that's it");
        // Broadcast it so it is only available to the RN Application
        LocalBroadcastManager
          .getInstance(mContext)
          .sendBroadcast(messagingEvent);
      } else {
        try {
          // If the app is in the background we send it to the Headless JS Service
          Intent headlessIntent = new Intent(
            mContext,
            LocationAlarmBackgroundMessagingService.class
          );
          headlessIntent.putExtra("my-extra-data", "that's it");
          ComponentName name = mContext.startService(headlessIntent);
          if (name != null) {
            HeadlessJsTaskService.acquireWakeLockNow(mContext);
          }
        } catch (IllegalStateException ex) {
          Log.e(
            TAG,
            "Background messages will only work if the message priority is set to 'high'",
            ex
          );
        }
      }
    }
  }
}
