package com.shivam.reminderapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "Log";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: "+ System.currentTimeMillis());
        ReminderOperations.checkFoReminderList(context);
        ReminderOperations.deleteInvalidReminder(context);
    }
}
