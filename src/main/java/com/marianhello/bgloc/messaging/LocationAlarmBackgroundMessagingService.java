package com.marianhello.bgloc.messaging;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.util.Log;

import com.facebook.react.HeadlessJsTaskService;
import android.os.Bundle;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;

import javax.annotation.Nullable;

public class LocationAlarmBackgroundMessagingService extends HeadlessJsTaskService {
  @Override
  protected @Nullable
  HeadlessJsTaskConfig getTaskConfig(Intent intent) {
    Bundle extras = intent.getExtras();
    if (extras != null) {
      return new HeadlessJsTaskConfig(
        "AlarmEventBackgroundMessage",
        Arguments.fromBundle(extras),
        60000,
        true /* allow to run in foreground */
      );
    }
    return null;
  }
}
